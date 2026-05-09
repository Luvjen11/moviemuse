import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getMovieById, getRecommendationsByMovieId } from '../services/api';
import './MovieDetail.css';

const MovieDetail = () => {
  const { id } = useParams();
  const [movie, setMovie] = useState(null);
  const [recommendations, setRecommendations] = useState([]);
  const [recsLoading, setRecsLoading] = useState(true);
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

  useEffect(() => {
    if (!id) return;
    let cancelled = false;
    const fetchRecs = async () => {
      setRecsLoading(true);
      try {
        const data = await getRecommendationsByMovieId(id, {
          topN: 10,
          excludeTitle: true,
        });
        if (!cancelled) {
          setRecommendations(Array.isArray(data?.recommendations) ? data.recommendations : []);
        }
      } catch (err) {
        console.error(err);
        if (!cancelled) setRecommendations([]);
      } finally {
        if (!cancelled) setRecsLoading(false);
      }
    };
    fetchRecs();
    return () => {
      cancelled = true;
    };
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
            src={movie.imageURL|| 'https://via.placeholder.com/300x450?text=No+Image'} 
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

          {movie.description && movie.description.trim() !== '' && (
            <section className="movie-description-section" aria-labelledby="movie-overview-heading">
              <h2 id="movie-overview-heading" className="movie-description-title">
                Overview
              </h2>
              <p className="movie-description-text">{movie.description}</p>
            </section>
          )}
          
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

      <div className="movie-recommendations-section">
        <h2 className="recommendations-title">Recommendations</h2>
        {recsLoading ? (
          <p className="recommendations-hint">Loading suggestions…</p>
        ) : recommendations.length === 0 ? (
          <p className="recommendations-hint">
            No recommendations right now (is the Python recommender running on port 8001?).
          </p>
        ) : (
          <div className="recommendations-list">
            {recommendations.map((rec) => (
              <div
                key={rec.anilistId ?? rec.title}
                className="recommendation-card"
              >
                <img
                  src={
                    rec.imageUrl ||
                    'https://via.placeholder.com/120x170?text=No+Poster'
                  }
                  alt={rec.title}
                  className="recommendation-image"
                />
                <p className="recommendation-title">{rec.title}</p>
              </div>
            ))}
          </div>
        )}
      </div>
      
      <Link to="/" className="back-to-home">
        Back to Home
      </Link>
    </div>
  );
};

export default MovieDetail;