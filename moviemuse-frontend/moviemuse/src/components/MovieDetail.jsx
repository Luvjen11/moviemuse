import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getMovieById } from '../services/api';
import './MovieDetail.css';

const MovieDetail = () => {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  useEffect(() => {
    const fetchMovie = async () => {
      try {
        const response = await getMovieById(id);
        setMovie(response.data);
      } catch (err) {
        setError('Failed to load movie details');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchMovie();
  }, [id]);
  
  if (loading) {
    return (
      <div className="movie-detail-container">
        <div className="loading-container">
          <div className="loading-spinner"></div>
          <p>Loading movie details...</p>
        </div>
      </div>
    );
  }
  
  if (error || !movie) {
    return (
      <div className="movie-detail-container">
        <div className="error-message">
          {error || 'Movie not found'}
          <Link to="/" className="back-link">Back to Home</Link>
        </div>
      </div>
    );
  }
  
  return (
    <div className="movie-detail-container">
      <div className="movie-detail-header">
        <div className="movie-poster-container">
          <img 
            src={movie.poster || 'https://via.placeholder.com/300x450?text=No+Image'} 
            alt={movie.title} 
            className="movie-poster"
          />
        </div>
        <div className="movie-info">
          <h1 className="movie-title">{movie.title}</h1>
          
          {movie.episodes > 0 && (
            <div className="movie-episodes">
              <span>{movie.episodes} episodes</span>
            </div>
          )}
          
          <div className="movie-genres">
            {movie.genres && movie.genres.map((genre, index) => (
              <span key={index} className="genre-tag">{genre}</span>
            ))}
          </div>
          
          <div className="movie-categories">
            {movie.category && movie.category.map((category, index) => (
              <span key={index} className="category-tag">{category}</span>
            ))}
          </div>
        </div>
      </div>
      
      <div className="movie-reviews-section">
        <h2 className="reviews-title">Reviews</h2>
        
        {movie.reviews && movie.reviews.length > 0 ? (
          <div className="reviews-list">
            {movie.reviews.map(review => (
              <div key={review.id} className="review-card">
                <div className="review-header">
                  <span className="review-rating">{review.rating}/5</span>
                </div>
                <p className="review-content">{review.content}</p>
              </div>
            ))}
          </div>
        ) : (
          <p className="no-reviews">No reviews yet. Be the first to add a review!</p>
        )}
        
        <Link to={`/movie/${movie.id}/add-review`} className="add-review-button">
          Add Review
        </Link>
      </div>
      
      <Link to="/" className="back-to-home">
        Back to Home
      </Link>
    </div>
  );
};

export default MovieDetail;