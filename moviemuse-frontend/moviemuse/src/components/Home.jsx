import React, { useEffect, useState } from "react";
import { getAllMovies } from "../services/api";
import MovieCard from "./MovieCard";
import { Link } from "react-router-dom";
import "./Home.css";

const Home = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    getMovies();
  }, []);

  const getMovies = async () => {
    try {
      setLoading(true);
      const data = await getAllMovies();
      setMovies(data);
      setError(null);
    } catch (error) {
      setError(error.message || "Something went wrong");
      console.error("Error fetching movies:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="home">
      <div className="container">
        <div className="home-header">
          <h1 className="page-title">MovieMuse</h1>
          <p className="page-subtitle">My Movie Collection</p>
        </div>
        <div className="add-movie-button-container">
          <Link to="/add-movie" className="add-movie-button">
            Add New Movie
          </Link>
        </div>
        <section className="movie-section">
          <h2 className="section-title">All Movies</h2>
          {loading ? (
            <div className="loading-container">
              <div className="loading-spinner"></div>
              <p>Loading your collection...</p>
            </div>
          ) : error ? (
            <div className="error-message">{error}</div>
          ) : (
            <div className="movies-gallery">
              {movies.length > 0 ? (
                movies.map((movie) => (
                  <Link to={`/movie/${movie.id}`} key={movie.id} className="movie-link">
                    <MovieCard movie={movie} />
                  </Link>
                ))
              ) : (
                <p className="no-movies">
                  No movies found. Add some to your collection!
                </p>
              )}
            </div>
          )}
        </section>
      </div>
    </div>
  );
};

export default Home;
