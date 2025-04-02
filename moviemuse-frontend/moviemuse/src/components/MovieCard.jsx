import React from 'react';
import './MovieCard.css';

const MovieCard = ({ movie }) => {
  return (
    <div className="movie-card">
      <div className="movie-card-image">
        <img 
          src={movie.poster || 'https://via.placeholder.com/300x450?text=No+Image'} 
          alt={movie.title} 
          className="movie-poster-img"
        />
        {movie.episodes > 0 && (
          <span className="movie-card-episodes">{movie.episodes} eps</span>
        )}
      </div>
      <div className="movie-card-content">
        <h3 className="movie-card-title">{movie.title}</h3>
        <div className="movie-card-genres">
          {movie.genres && movie.genres.map((genre, index) => (
            <span key={index} className="movie-card-genre">{genre}</span>
          ))}
        </div>
      </div>
    </div>
  );
};

export default MovieCard;
