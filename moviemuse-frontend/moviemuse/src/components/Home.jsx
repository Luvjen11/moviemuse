import React, { useEffect, useState } from "react";
import { deleteMovie, getAllMovies } from "../services/api";
import MovieCard from "./MovieCard";
import { Link } from "react-router-dom";
import "./Home.css";

const Home = () => {
  const [movies, setMovies] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [deleteLoading,setDeleteLoading] = useState(null);
  const [filterType, setFilterType] = useState("ALL");

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

  const handleDelete = async (id)=> {
    if (window.confirm('Are you sure you want to delete this movie?')) {
      try {
        // set the loading state for the specific quote
        setDeleteLoading(id);
        await deleteMovie(id);
        console.log("Deleting movie id:", id, typeof id);


        // remove the deleted movie from the state
        setMovies(movies.filter(movie => movie.id !== id));
      } catch (error) {
        console.error('Error deleting movie:', error);
        alert(`Failed to delete movie: ${error.message}`);
      } finally {
        setDeleteLoading(null);
      }
    }
  }

  const filterMovie = movies.filter((movie) => {
    if (filterType === "ALL") return true;
    if (!movie.type) return false;
    return movie.type.toUpperCase() === filterType;
  })

  return (
    <div className="home">
      <div className="container">
        <div className="home-header">
          <h1 className="page-title">MovieMuse</h1>
          <p className="page-subtitle">My Movie Collection</p>
        </div>
        <div className="filters-container">
          <button
            className={`filter-button ${filterType === "ALL" ? "active" : ""}`}
            onClick={() => setFilterType("ALL")}
          >
            All
          </button>
          <button
            className={`filter-button ${filterType === "ANIME" ? "active" : ""}`}
            onClick={() => setFilterType("ANIME")}
          >
            Anime
          </button>
          <button
            className={`filter-button ${filterType === "KDRAMA" ? "active" : ""}`}
            onClick={() => setFilterType("KDRAMA")}
          >
            K-Drama
          </button>
          <button
            className={`filter-button ${filterType === "MOVIE" ? "active" : ""}`}
            onClick={() => setFilterType("MOVIE")}
          >
            Movie
          </button>
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
              {filterMovie.length > 0 ? (
                filterMovie.map((movie) => (
                  <div key={movie.id} className="movie-card-container">
                    <Link to={`/movie/${movie.id}`} className="movie-link">
                      <MovieCard 
                        movie={movie} 
                        isLoading={deleteLoading === movie.id}
                      />
                    </Link>
                    <button
                      className="delete-button"
                      onClick={(e) => {
                        e.preventDefault();
                        handleDelete(movie.id);
                      }}
                      disabled={deleteLoading === movie.id}
                    >
                      ×
                    </button>
                  </div>
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
