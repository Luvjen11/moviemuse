# Deploy MovieMuse with Aiven and Render

This guide deploys MovieMuse using:

- **Aiven MySQL** for the database
- **Render Web Service** for the Spring Boot API
- **Render Web Service** for the Python/FastAPI recommender
- **Render Static Site** for the React/Vite frontend

The most important rule is: export and verify your current local database before changing or deleting anything.

## 1. Deployment architecture

| Component | Location | Purpose |
|---|---|---|
| MySQL | Aiven | Stores movies, genres, reviews, and watchlist values |
| Spring Boot | Render Web Service | Main MovieMuse API at `/moviemuse` |
| FastAPI | Render Web Service | Anime recommendations at `/recommend` |
| React/Vite | Render Static Site | Browser frontend |

The application still calls external TMDB and AniList APIs. Those are not Azure resources, but the backend needs internet access and valid API configuration.

## 2. Prepare the repository

Work from the restored local `main` branch:

```powershell
git status --short --branch
git branch --show-current
```

Do not commit:

- `moviemuse-backend/moviemuse/src/main/resources/local.properties`
- database dumps such as `moviemuse.sql`
- API keys or database passwords

Push the current project to GitHub after the deployment changes below are made.

## 3. Make the two required deployment changes

### 3.1 Make the frontend API URL configurable

Open `moviemuse-frontend/moviemuse/src/services/api.js` and replace the hardcoded API URL:

```javascript
const API_BASE_URL = "http://localhost:8080/moviemuse";
```

with:

```javascript
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/moviemuse";
```

The fallback keeps local development working. Render will provide the production value during the Vite build.

### 3.2 Allow Render to provide the backend port

Add this line to `moviemuse-backend/moviemuse/src/main/resources/application.properties`:

```properties
server.port=${PORT:8080}
```

Render supplies `PORT` automatically. Do not hardcode a production port.

## 4. Export the current local database

Run these commands from a terminal where the MySQL client tools are installed. This creates a dump without `CREATE DATABASE` or `USE` statements, which makes it easier to import into Aiven's existing database.

```powershell
cd C:\Users\jenni\Documents\personal\moviemuse
mysqldump --host=127.0.0.1 --port=3306 --user=root --single-transaction --routines --triggers moviemuse > moviemuse-backup.sql
```

Enter the local MySQL password when prompted.

Check that the dump exists and is not empty:

```powershell
Get-Item .\moviemuse-backup.sql
```

Keep this backup somewhere safe. Do not commit it to GitHub.

If `mysqldump` is not recognized, use the full path to the MySQL `bin` folder or install the MySQL client tools. The command must be run against the database that contains your current MovieMuse data.

## 5. Create the Aiven MySQL service

1. Sign in to your existing Aiven account.
2. Create a new **MySQL** service.
3. Choose the free plan if it is available for your account and region.
4. Select a region close to the Render services.
5. Wait until the service is **Running**.
6. Open **Connection information** and record:
   - Host
   - Port
   - Database name, often `defaultdb`
   - Username, often `avnadmin`
   - Password

Do not use the local database password unless you intentionally want to. Aiven supplies its own credentials.

## 6. Import the database into Aiven

Aiven provides a MySQL host and a non-standard port. Use the values shown in the Aiven console.

```powershell
mysql --host=YOUR_AIVEN_HOST --port=YOUR_AIVEN_PORT --user=YOUR_AIVEN_USERNAME --ssl-mode=REQUIRED --password YOUR_AIVEN_DATABASE < .\moviemuse-backup.sql
```

When prompted, enter the Aiven password. The final positional database name can be required by some MySQL clients:

```powershell
mysql --host=YOUR_AIVEN_HOST --port=YOUR_AIVEN_PORT --user=YOUR_AIVEN_USERNAME --ssl-mode=REQUIRED --password YOUR_AIVEN_DATABASE < .\moviemuse-backup.sql
```

Use the command form accepted by your installed MySQL client. Never put the password directly in a command that may be saved in shell history.

Verify the import using an Aiven SQL console or MySQL client:

```sql
SHOW TABLES;
SELECT COUNT(*) FROM movies;
SELECT COUNT(*) FROM reviews;
```

Do not shut down the local database or delete the backup until these counts match your local database.

## 7. Create the Render backend service

In Render:

1. Open **New** and choose **Web Service**.
2. Connect the GitHub repository.
3. Set the service root directory to:

```text
moviemuse-backend/moviemuse
```

4. Use these settings:

```text
Build command: ./mvnw clean package -DskipTests
Start command: java -jar target/moviemuse-0.0.1-SNAPSHOT.jar
```

If Render reports that `mvnw` is not executable, run this locally and commit the permission change:

```powershell
git update-index --chmod=+x moviemuse-backend/moviemuse/mvnw
git commit -m "Make Maven wrapper executable"
git push
```

Add these Render environment variables:

```text
SPRING_DATASOURCE_URL=jdbc:mysql://YOUR_AIVEN_HOST:YOUR_AIVEN_PORT/YOUR_AIVEN_DATABASE?sslMode=REQUIRED
SPRING_DATASOURCE_USERNAME=YOUR_AIVEN_USERNAME
SPRING_DATASOURCE_PASSWORD=YOUR_AIVEN_PASSWORD
TMDB_API_KEY=YOUR_TMDB_API_KEY
```

Do not add `PORT`; Render supplies it.

Do not add `SPRING_CONFIG_IMPORT` for the local file. The local `local.properties` file is optional and should not be present in the deployed artifact.

The backend route will be:

```text
https://YOUR_BACKEND.onrender.com/moviemuse
```

Check that the API returns your imported titles:

```text
https://YOUR_BACKEND.onrender.com/moviemuse
```

## 8. Create the Render recommender service

Create a second Render **Web Service** from the same GitHub repository.

Use these settings:

```text
Root directory: recommender-service
Build command: pip install -r requirements.txt
Start command: uvicorn app:app --host 0.0.0.0 --port $PORT
```

After deployment, test:

```text
https://YOUR_RECOMMENDER.onrender.com/health
```

Expected response:

```json
{"status":"ok"}
```

The recommender loads `notebook/anilist_5000_with_image.json` using the repository-relative path already defined in `recommender-service/app.py`. Keep that dataset in GitHub or change the service to use another hosted dataset.

## 9. Connect Spring Boot to the recommender

Add this environment variable to the Render backend service:

```text
RECOMMENDER_BASE_URL=https://YOUR_RECOMMENDER.onrender.com
```

Spring will call:

```text
POST https://YOUR_RECOMMENDER.onrender.com/recommend
```

The recommender can sleep on the free Render plan. The first recommendation request may therefore be slow while it starts.

## 10. Create the Render frontend static site

Create a Render **Static Site** from the same GitHub repository.

Use these settings:

```text
Root directory: moviemuse-frontend/moviemuse
Build command: npm ci && npm run build
Publish directory: dist
```

Add this environment variable before deploying:

```text
VITE_API_BASE_URL=https://YOUR_BACKEND.onrender.com/moviemuse
```

The value must include `/moviemuse` at the end. Do not add a trailing slash after it.

## 11. CORS

The current controllers use `@CrossOrigin("*")`, so the frontend should be able to call the backend immediately. After the deployment works, replace the wildcard with the exact Render frontend origin for better security.

For example:

```text
https://YOUR_FRONTEND.onrender.com
```

Do not include a trailing slash.

## 12. Test the complete deployment

Test in this order:

1. Open the recommender `/health` endpoint.
2. Open the backend `/moviemuse` endpoint and confirm your imported movies exist.
3. Open the Render frontend URL.
4. Load the home page.
5. Open a movie detail page.
6. Create and edit a review.
7. Add a movie manually.
8. Search and import a TMDB movie or TV show.
9. Search and import an AniList anime.
10. Open recommendations for an anime title.
11. Refresh the browser and confirm data remains in Aiven.

## 13. Cutover and cleanup

Keep the local database and `moviemuse-backup.sql` until the deployed app passes all tests.

After verification:

- Stop any old Azure services still running.
- Remove old Azure deployment workflows if they are no longer needed.
- Keep the local `main` branch as the source of truth.
- Rotate any API keys or database passwords that were exposed in old configuration files.
- Do not delete the Aiven service; it is now the production database.

## Troubleshooting

### Backend cannot connect to Aiven

Check:

- The JDBC URL starts with `jdbc:mysql://`.
- The Aiven host and port are correct.
- The URL includes `?sslMode=REQUIRED`.
- The Aiven service is running.
- The Render environment variable names start with `SPRING_DATASOURCE_`.

### Frontend still calls localhost

Confirm that:

- `api.js` uses `import.meta.env.VITE_API_BASE_URL`.
- `VITE_API_BASE_URL` is set on the Render Static Site.
- The frontend was rebuilt after adding the variable.
- The URL includes `/moviemuse`.

### Recommendations fail

Check:

- The recommender `/health` endpoint works.
- `RECOMMENDER_BASE_URL` points to the recommender root URL without `/recommend`.
- The dataset file is present in the deployed repository.
- The recommender service has finished waking up.

### The database is empty

Do not allow Hibernate to create a new empty database until you have confirmed the import target. Re-run the Aiven import and verify `SELECT COUNT(*) FROM movies;`.
