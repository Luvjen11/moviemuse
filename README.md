# **MovieMuse**

*Your personal movie collection and review platform*

## Overview

MovieMuse is a full-stack web application for building and managing a personal collection of movies, anime, and K-dramas. Add titles by hand, import from **TMDB** (movies and TV series used as **K-dramas**), pull anime from **AniList**, view genres and overviews on the detail page, filter by type on the home screen, and write or edit reviews.

## Features

- **Movie Management** – Add, view, and delete movies in your collection
- **Content Types** – Movie, Anime, K-Drama (filters on the home page)
- **Import from TMDB (movies)** – Search and import feature films (`source`: `TMDB`)
- **Import from TMDB (K-drama / TV)** – Search TMDB TV; import saves as **`ContentType.KDRAMA`** with **`source`** `TMDB_TV` so TV IDs never collide with film IDs  
  On **Add**, one search fills two labeled lists (**K-drama (TV)** vs **Movies**); hover the Import buttons for tooltip hints (`Saved as …`)
- **Import from AniList** – Search and import anime (poster, genres, description cap for MySQL-safe length)
- **Anime recommendations** – TF-IDF via a Python service; Spring exposes `GET /moviemuse/recommendations/{id}`
- **Backfill anime descriptions** – POST endpoint to refill missing AniList descriptions by `externalId`
- **Manual Entry** – Posters via file upload or URL, genres, categories, type
- **Reviews** – Create / update ratings and text per title


## Getting Started

### 1. Clone the Repository

```sh
git clone https://github.com/yourusername/moviemuse.git
cd moviemuse
```

### 2. Backend (Spring Boot + MySQL)

1. Go to the backend module:

```sh
cd moviemuse-backend/moviemuse
```

2. Create a **`local.properties`** file (not committed) with your DB settings:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moviemuse
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **TMDB:** One key powers both movie and TV import. From [TMDB API settings](https://www.themoviedb.org/settings/api):

```properties
tmdb.api.key=your_tmdb_api_key
```

4. **Optional – Recommendations:** Start the Python service and configure:

```properties
recommender.base.url=http://localhost:8001
```

5. Run:

```sh
./mvnw spring-boot:run
```

Backend context path **`/moviemuse`** → **http://localhost:8080/moviemuse**.

### Recommender (Python, optional)

Anime “you might also like” on the title detail page.

1. Dataset: `notebook/anilist_5000_with_image.json` (path used by `recommender-service/app.py`).
2. From repo root:

```sh
cd recommender-service
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8001
```

Health: **`GET http://localhost:8001/health`**  

### 3. Frontend (React + Vite)

```sh
cd moviemuse-frontend/moviemuse
npm install
npm run dev
```

Use the URL Vite prints (often **http://localhost:5173**).

**Production API:** Open **`src/services/api.js`** and set **`API_BASE_URL`** to your public backend including `/moviemuse` before `npm run build`, or refactor that line to use `import.meta.env.VITE_API_BASE_URL` with a **`VITE_…`** env var at build time in your host (Static Site CI).

## Tech Stack

- **Frontend:** React 19, React Router, Vite, Axios, CSS  
- **Backend:** Java 21, Spring Boot, Spring Data JPA, WebClient  
- **Database:** MySQL  
- **Recommender:** Python, FastAPI, scikit-learn (TF-IDF)  
- **External APIs:** [TMDB](https://www.themoviedb.org/documentation/api) (`/movie/…`, `/search/movie`, `/search/tv`, `/tv/{id}`), [AniList](https://anilist.gitbook.io/anilist-apiv2-docs/) GraphQL

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/moviemuse` | List all titles |
| `GET` | `/moviemuse/{id}` | Get one by ID |
| `POST` | `/moviemuse` | Create (JSON) |
| `POST` | `/moviemuse/upload` | Create (multipart poster / URL, genres, type) |
| `DELETE` | `/moviemuse/{id}` | Delete title and reviews |
| `GET` | `/moviemuse/tmdb/search/movie?query=...` | TMDB movie search |
| `POST` | `/moviemuse/tmdb/import/{tmdbId}` | Import TMDB **movie** by ID |
| `GET` | `/moviemuse/tmdb/search/tv?query=...` | TMDB TV search |
| `POST` | `/moviemuse/tmdb/import/tv/{tmdbId}` | Import TMDB **TV** as K-drama (`TMDB_TV`) |
| `GET` | `/moviemuse/anilist/search/anime?query=...` | AniList search |
| `POST` | `/moviemuse/anilist/import` | Import anime (AniList shape) |
| `POST` | `/moviemuse/anilist/backfill-descriptions` | Backfill null descriptions for ANILIST rows |
| `GET` | `/moviemuse/recommendations/{movieId}` | Recommendations (`topN`, `excludeTitle` query params) |
| `POST` | `/moviemuse/review` | Create review |
| `PUT` | `/moviemuse/review/{id}` | Update review |

## Project Structure

```
moviemuse/
├── moviemuse-backend/moviemuse/     # Spring Boot
│   └── src/main/java/.../moviemuse/
│       ├── controller/              # Movie, Tmdb, AniList, Review, Recommendation
│       ├── service/
│       ├── repository/
│       ├── model/
│       └── dto/                     # TMDB (movie + TV), AniList, etc.
├── moviemuse-frontend/moviemuse/       # Vite + React
│   └── src/
│       ├── components/
│       └── services/api.js
├── recommender-service/
├── notebook/
└── README.md
```

## K-drama (current vs optional)

**Shipped:** K-drama import goes through **TMDB TV**: search/import endpoints above; stored with **`TMDB_TV`** + **`ContentType.KDRAMA`**.


## Deploying on Render

| Piece | Render type | Notes |
|--------|-------------|--------|
| **MySQL** | Render MySQL or external | Drive `SPRING_DATASOURCE_*`. |
| **Spring Boot** | Web Service | Root `moviemuse-backend/moviemuse`; build `./mvnw clean package -DskipTests`; start correct `target/*.jar`; env keys for DB, **`TMDB_API_KEY`** / `tmdb.api.key` mapping, optional **`RECOMMENDER_BASE_URL`**. |
| **Python recommender** | Web Service | Root `recommender-service`; `uvicorn … --port $PORT`. |
| **Vite frontend** | Static Site | Root `moviemuse-frontend/moviemuse`; **`npm ci && npm run build`**; publish **`dist`**; set API base for production in **`api.js`** or **`VITE_API_BASE_URL`**. |



## Future Enhancements

- **TMDB discover / KR-only TV** – Browse Korean TV from `discover/tv` or filter search results  
- **MyDramaList** – Alternate metadata source when API access is available  
- **NMF / diversification** – Recommender variants 
- **User accounts & watchlist** – Use `Movie.inWatchlist` from the UI  

## License

This project is open-source and available under the [MIT License](LICENSE).

*Build your movie, anime, and K-drama collection with MovieMuse.*
