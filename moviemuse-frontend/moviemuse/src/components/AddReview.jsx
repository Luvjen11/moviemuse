import React, { useState } from 'react';
import { createReview } from '../services/api';
import { useNavigate, useParams } from 'react-router-dom';
import './AddReview.css';

const AddReview = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [review, setReview] = useState({
    content: '',
    rating: 5,
    movieId: id
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setReview(prev => ({
      ...prev,
      [name]: name === 'rating' ? parseInt(value) : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      await createReview(review);
      navigate(`/movie/${id}`);
    } catch (error) {
      setError(error.message || 'Failed to add review');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="add-review-container">
      <h2 className="add-review-title">Add Review</h2>
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="review-form">
        <div className="form-group">
          <label htmlFor="rating">Rating</label>
          <select
            id="rating"
            name="rating"
            value={review.rating}
            onChange={handleChange}
            required
          >
            <option value="5">5 - Excellent</option>
            <option value="4">4 - Very Good</option>
            <option value="3">3 - Good</option>
            <option value="2">2 - Fair</option>
            <option value="1">1 - Poor</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="content">Your Review</label>
          <textarea
            id="content"
            name="content"
            value={review.content}
            onChange={handleChange}
            required
            placeholder="Write your review here..."
            rows="5"
          />
        </div>

        <div className="form-actions">
          <button 
            type="button" 
            className="cancel-button"
            onClick={() => navigate(`/movie/${id}`)}
          >
            Cancel
          </button>
          <button 
            type="submit" 
            className="submit-button"
            disabled={loading}
          >
            {loading ? 'Adding Review...' : 'Add Review'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddReview;