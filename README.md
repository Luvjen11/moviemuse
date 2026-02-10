# **MovieMuse**

*Your personal movie collection and review platform*

## Overview

MovieMuse is a full-stack web application for building and managing a personal collection of movies, anime, and K-dramas. Add titles by hand, import from **TMDB** (movies) or **AniList** (anime), view genres, filter by type, and write or edit reviews.

## Features

- **Movie Management** – Add, view, and delete movies in your collection
- **Content Types** – Organize by type: Movie, Anime, K-Drama (filter on the home page)
- **Import from TMDB** – Search and import movies with metadata (poster, overview, genres) from [The Movie Database](https://www.themoviedb.org/)
- **Import from AniList** – Search and import anime with poster and genres from [AniList](https://anilist.co/)
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

4. Run the app:

```sh
./mvnw spring-boot:run
```

Backend runs at **http://localhost:8080**.

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

## Tech Stack

- **Frontend:** React 19, React Router, Vite, Axios, CSS
- **Backend:** Java 17+, Spring Boot, Spring Data JPA, WebClient
- **Database:** MySQL
- **External APIs:** TMDB (movies), AniList GraphQL (anime)

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
| `POST` | `/moviemuse/review` | Create a review |
| `PUT` | `/moviemuse/review/{id}` | Update a review |

## Project Structure

```
moviemuse/
├── moviemuse-backend/moviemuse/   # Spring Boot app
│   └── src/main/java/.../moviemuse/
│       ├── controller/            # Movie, Tmdb, AniList
│       ├── service/               # Movie, Tmdb, AniList
│       ├── repository/            # Movie, Review
│       ├── model/                 # Movie, Review, ContentType
│       └── dto/                   # TMDB & AniList DTOs
├── moviemuse-frontend/moviemuse/  # Vite + React
│   └── src/
│       ├── components/            # Home, NewMovie, MovieDetail, etc.
│       └── services/api.js        # API client
└── README.md
```

## Future Enhancements

- **Recommendation system** – Content-based “similar movies” (see `RECOMMENDATION_SYSTEM_PLAN.md` for a phased plan)
- **User accounts** – Per-user collections and preferences
- **Watchlist** – Mark titles you want to watch
- **Advanced search** – By title, genre, or rating

## License

This project is open-source and available under the [MIT License](LICENSE).

*Build your movie and anime collection with MovieMuse.*
