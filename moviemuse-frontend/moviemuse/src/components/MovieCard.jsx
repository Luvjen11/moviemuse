import React from "react";
import { Link } from "react-router-dom";

const MovieCard = ({ movie }) => {
  return (
    <div className="movie-card">
      <Link to={`/moviemuse/${movie.id}`}>
        <div className="movie-card-image">
          <img src={movie.poster} alt={movie.title} />
          {movie.episodes > 0 && (
            <span className="movie-card-episodes">{movie.episodes}</span>
          )}
        </div>
        <div className="movie-card-content">
          <h3 className="movie-card-title">{movie.title}</h3>
          <div className="movie-card-genres">
            {movie.genres &&
              movie.genres.map((genre, index) => (
                <span key={index} className="movie-card-genre">
                  {genre}
                </span>
              ))}
          </div>
        </div>
      </Link>
    </div>
  );
};

export default MovieCard;
