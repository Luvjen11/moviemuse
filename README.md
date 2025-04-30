# **MovieMuse**

*Your personal movie collection and review platform*

## Overview

MovieMuse is a full-stack web application that allows users to create and manage a collection of movies, add reviews, and categorize films by genres and categories. It provides a seamless experience for movie enthusiasts to keep track of their favorite films and share their thoughts.

## Features

- **Movie Management** – Add, view, and delete movies in your collection
- **Image Upload** – Add movie posters via file upload or URL
- **Categorization** – Organize movies with genres and categories
- **Reviews** – Write and update reviews for any movie in your collection
- **Episode Tracking** – Keep track of TV shows with episode counts
- **Responsive Design** – Enjoy a seamless experience across devices

## Getting Started

### 1. Clone the Repository

```sh
git clone https://github.com/yourusername/moviemuse.git
cd moviemuse
```

### 2. Backend Setup (Spring Boot + MySQL)

1. Navigate to the backend directory:

```sh
cd moviemuse-backend/moviemuse
```

2. Configure your MySQL database in `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

3. Create a `local.properties` file in the same directory with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moviemuse
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. Build and run the Spring Boot application:

```sh
./mvnw spring-boot:run
```

### 3. Frontend Setup (React)

1. Navigate to the frontend directory:

```sh
cd moviemuse-frontend/moviemuse
```

2. Install dependencies:

```sh
npm install
```

3. Start the development server:

```sh
npm start
```

4. Open your browser and visit `http://localhost:3000`

## Tech Stack

- **Frontend**: React, CSS
- **Backend**: Java, Spring Boot
- **Database**: MySQL
- **API Handling**: Axios
- **Image Storage**: Base64 encoding and file uploads

## API Endpoints

- `GET /moviemuse` – Retrieve all movies
- `GET /moviemuse/{id}` – Get a specific movie by ID
- `POST /moviemuse` – Add a new movie (JSON)
- `POST /moviemuse/upload` – Add a new movie with file upload
- `DELETE /moviemuse/{id}` – Remove a movie by ID
- `GET /moviemuse/category/{category}` – Get movies by category
- `POST /moviemuse/review` – Add a new review
- `PUT /moviemuse/review/{id}` – Update an existing review

## Future Enhancements

- **User Authentication** – Personal movie collections
- **Advanced Search** – Find movies by title, genre, or rating
- **Watchlist** – Track movies you want to watch
- **Social Sharing** – Share your reviews with friends
- **External API Integration** – Fetch movie data from public APIs

## License

This project is open-source and available under the [MIT License](LICENSE).

*Start building your movie collection today with MovieMuse!*
