# **MovieMuse**

*Your personal movie collection and review platform*

## Overview

MovieMuse is a full-stack web application for building and managing a personal collection of movies, anime, and K-dramas. Add titles by hand, import from **TMDB** (movies) or **AniList** (anime), view genres, filter by type, and write or edit reviews.

## Features

- **Movie Management** – Add, view, and delete movies in your collection
- **Content Types** – Organize by type: Movie, Anime, K-Drama (filter on the home page)
- **Import from TMDB** – Search and import movies with metadata (poster, overview, genres) from [The Movie Database](https://www.themoviedb.org/)
- **Import from AniList** – Search and import anime with poster and genres from [AniList](https://anilist.co/)
- **Anime recommendations** – TF-IDF similarity via a small Python service; Spring proxies `GET /moviemuse/recommendations/{id}` to the recommender 
- **Backfill anime descriptions** – One-shot API to fetch missing descriptions from AniList by `externalId` (for older imports / recommendations)
- **Manual Entry** – Add titles with poster (file upload or URL), genres, and type
- **Genres** – Genres are stored and shown on cards and movie detail
- **Reviews** – Add and update reviews (with rating) for any title
- **Episode Tracking** – Episode count for anime and series
- **Responsive UI** – React frontend with Vite

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

2. Create a **`local.properties`** file in the same directory (not committed) with your settings:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moviemuse
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. **Optional – TMDB import:** To use “Import from TMDB”, add your [TMDB API key](https://www.themoviedb.org/settings/api):

```properties
tmdb.api.key=your_tmdb_api_key
```

4. **Optional – Recommendations:** Start the Python recommender and point the backend at it (see [Recommender](#recommender-python-service--anime-suggestions)):

```properties
recommender.base.url=http://localhost:8001
```

5. Run the app:

```sh
./mvnw spring-boot:run
```

Backend runs at **http://localhost:8080**.

### Recommender (Python, optional)

Used for **anime “you might also like”** on the movie detail page.

1. Dataset: `notebook/anilist_5000_with_image.json` (path is resolved from `recommender-service/app.py`).
2. From repo root:

```sh
cd recommender-service
pip install -r requirements.txt
uvicorn app:app --host 0.0.0.0 --port 8001
```

3. Health check: `GET http://localhost:8001/health`  
   More detail: [`RECOMMENDER_INTEGRATION_GUIDE.md`](RECOMMENDER_INTEGRATION_GUIDE.md).

### 3. Frontend (React + Vite)

1. Go to the frontend app:

```sh
cd moviemuse-frontend/moviemuse
```

2. Install dependencies:

```sh
npm install
```

3. Start the dev server:

```sh
npm run dev
```

4. Open **http://localhost:3000** (or the port Vite prints).

5. **Production API URL:** Point the UI at your deployed API (must include the `/moviemuse` path), e.g. build with  
   `VITE_API_BASE_URL=https://your-backend.onrender.com/moviemuse`  
   (falls back to `http://localhost:8080/moviemuse` when unset — see `src/services/api.js`).

## Tech Stack

- **Frontend:** React 19, React Router, Vite, Axios, CSS
- **Backend:** Java 21, Spring Boot, Spring Data JPA, WebClient
- **Database:** MySQL
- **ML / recommender:** Python, FastAPI, scikit-learn (TF-IDF)
- **External APIs:** TMDB (movies), AniList GraphQL (anime); K-drama via [MyDramaList API](https://mydramalist.github.io/MDL-API/) (planned — see below)

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/moviemuse` | List all movies |
| `GET` | `/moviemuse/{id}` | Get one movie by ID |
| `POST` | `/moviemuse` | Create a movie (JSON) |
| `POST` | `/moviemuse/upload` | Create a movie (multipart: poster file/URL, genres, type) |
| `DELETE` | `/moviemuse/{id}` | Delete a movie (and its reviews) |
| `GET` | `/moviemuse/tmdb/search/movie?query=...` | Search TMDB by title |
| `POST` | `/moviemuse/tmdb/import/{tmdbId}` | Import a movie from TMDB by ID |
| `GET` | `/moviemuse/anilist/search/anime?query=...` | Search AniList anime |
| `POST` | `/moviemuse/anilist/import` | Import an anime (body: AniList anime object) |
| `POST` | `/moviemuse/anilist/backfill-descriptions` | Fill `description` for ANILIST anime where it is null (calls AniList by id) |
| `GET` | `/moviemuse/recommendations/{movieId}?topN=10&excludeTitle=true` | Anime recommendations (proxies to Python recommender) |
| `POST` | `/moviemuse/review` | Create a review |
| `PUT` | `/moviemuse/review/{id}` | Update a review |

## Project Structure

```
moviemuse/
├── moviemuse-backend/moviemuse/   # Spring Boot app
│   └── src/main/java/.../moviemuse/
│       ├── controller/            # Movie, Tmdb, AniList, Recommendation
│       ├── service/               # Movie, Tmdb, AniList, Recommender
│       ├── repository/            # Movie, Review
│       ├── model/                 # Movie, Review, ContentType
│       └── dto/                   # TMDB, AniList, recommendation DTOs
├── moviemuse-frontend/moviemuse/  # Vite + React
│   └── src/
│       ├── components/            # Home, NewMovie, MovieDetail, etc.
│       └── services/api.js        # API client
├── recommender-service/           # FastAPI + TF-IDF
├── notebook/                      # Data + experiments
└── README.md
```

## K-drama import (MyDramaList API) — next implementation

[MyDramaList](https://mydramalist.com/) documents a **v1 HTTP API** (search titles, get title by id, ratings). Official reference: [MDL API docs](https://mydramalist.github.io/MDL-API/). You typically need an **API key** (header `mdl-api-key` or OAuth). Read [Terms of Use](https://mydramalist.com/terms) before production use.

## Deploying on Render

Render fits this stack as **multiple services** (no single “Deploy” button for the whole monorepo unless you script it).

| Piece | Render service type | Notes |
|--------|---------------------|--------|
| **MySQL** | [Render MySQL](https://render.com/docs/databases) or external (PlanetScale, etc.) | Set `spring.datasource.*` from the dashboard env vars. |
| **Spring Boot** | **Web Service** | Root: `moviemuse-backend/moviemuse`. Build: `./mvnw clean package -DskipTests`. Start: `java -jar target/moviemuse-0.0.1-SNAPSHOT.jar` (confirm JAR name from `target/`). Env: `SPRING_DATASOURCE_URL`, user, password, `TMDB_API_KEY`, `RECOMMENDER_BASE_URL`, future `MDL_API_KEY`. |
| **Python recommender** | **Web Service** | Root: `recommender-service`. Build: `pip install -r requirements.txt`. Start: `uvicorn app:app --host 0.0.0.0 --port $PORT`. Ensure dataset is available (e.g. commit a trimmed JSON or download at build — large files may need object storage). |
| **React (Vite)** | **Static Site** | Root: `moviemuse-frontend/moviemuse`. Build: `npm ci && npm run build`. Publish directory: `dist`. Set env `VITE_API_BASE_URL` to your **public** Spring URL. |


## Future Enhancements

- **MyDramaList K-drama import** – Search + import with `ContentType.KDRAMA` (see above)
- **NMF / diversification** – Alternate recommender models and less “sequel-heavy” suggestions ([`RECOMMENDER_INTEGRATION_GUIDE.md`](RECOMMENDER_INTEGRATION_GUIDE.md))
- **User accounts** – Per-user collections and preferences
- **Watchlist** – Use `Movie.inWatchlist` from the UI
- **Advanced search** – By title, genre, or rating

## License

This project is open-source and available under the [MIT License](LICENSE).

*Build your movie and anime collection with MovieMuse.*
