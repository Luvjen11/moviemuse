import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './NewMovie.css';
import { createMovie } from '../services/api';

const NewMovie = () => {
  const navigate = useNavigate();
  const [movie, setMovie] = useState({
    title: '',
    episodes: 0,
    poster: '',
    genres: [],
    category: []
  });
  const [posterFile, setPosterFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState('');
  const [genreInput, setGenreInput] = useState('');
  const [categoryInput, setCategoryInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setMovie(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setPosterFile(file);
      // Create a preview URL for the selected image
      const fileUrl = URL.createObjectURL(file);
      setPreviewUrl(fileUrl);
    }
  };

  const handleAddGenre = () => {
    if (genreInput.trim() && !movie.genres.includes(genreInput.trim())) {
      setMovie({
        ...movie,
        genres: [...movie.genres, genreInput.trim()]
      });
      setGenreInput('');
    }
  };

  const handleRemoveGenre = (genreToRemove) => {
    setMovie({
      ...movie,
      genres: movie.genres.filter(genre => genre !== genreToRemove)
    });
  };

  const handleAddCategory = () => {
    if (categoryInput.trim() && !movie.category.includes(categoryInput.trim())) {
      setMovie({
        ...movie,
        category: [...movie.category, categoryInput.trim()]
      });
      setCategoryInput('');
    }
  };

  const handleRemoveCategory = (categoryToRemove) => {
    setMovie({
      ...movie,
      category: movie.category.filter(category => category !== categoryToRemove)
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
        console.log("Submitting form with data:", movie);
        
        // Create a FormData object to send the file
        const formData = new FormData();
        formData.append('title', movie.title);
        formData.append('episodes', movie.episodes.toString());
        
        // Add the file if it exists
        if (posterFile) {
            formData.append('posterFile', posterFile);
        } else if (movie.poster) {
            // If no file but URL exists, send the URL
            formData.append('poster', movie.poster);
        }
        
        // Add genres as separate entries with the same name
        if (movie.genres && movie.genres.length > 0) {
            movie.genres.forEach(genre => {
                formData.append('genres', genre);
            });
        }
        
        // Add categories as separate entries with the same name
        if (movie.category && movie.category.length > 0) {
            movie.category.forEach(category => {
                formData.append('category', category);
            });
        }
        
        // Log the form data for debugging
        for (let [key, value] of formData.entries()) {
            console.log(`${key}: ${value}`);
        }

        await createMovie(formData);
        navigate('/');
    } catch (error) {
        setError(error.message || 'Something went wrong');
        console.error('Error details:', error);
    } finally {
        setLoading(false);
    }
};

  return (
    <div className="new-movie-container">
      <h2 className="new-movie-title">Add New Movie</h2>
      
      {error && <div className="error-message">{error}</div>}
      
      <form className="new-movie-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="title">Title</label>
          <input
            type="text"
            id="title"
            name="title"
            value={movie.title}
            onChange={handleChange}
            required
            placeholder="Enter movie title"
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="episodes">Episodes</label>
          <input
            type="number"
            id="episodes"
            name="episodes"
            value={movie.episodes}
            onChange={handleChange}
            min="0"
            placeholder="Number of episodes"
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="posterFile">Movie Poster</label>
          <div className="file-input-container">
            <input
              type="file"
              id="posterFile"
              name="posterFile"
              onChange={handleFileChange}
              accept="image/*"
              className="file-input"
            />
            <label htmlFor="posterFile" className="file-input-label">
              {posterFile ? posterFile.name : 'Choose an image file'}
            </label>
          </div>
          
          <div className="or-divider">
            <span>OR</span>
          </div>
          
          <input
            type="url"
            id="poster"
            name="poster"
            value={movie.poster}
            onChange={handleChange}
            placeholder="Enter poster image URL"
          />
          
          {previewUrl && (
            <div className="poster-preview">
              <img src={previewUrl} alt="Poster preview" />
            </div>
          )}
          {!previewUrl && movie.poster && (
            <div className="poster-preview">
              <img src={movie.poster} alt="Poster preview" />
            </div>
          )}
        </div>
        
        <div className="form-group">
          <label htmlFor="genres">Genres</label>
          <div className="input-with-button">
            <input
              type="text"
              id="genres"
              value={genreInput}
              onChange={(e) => setGenreInput(e.target.value)}
              placeholder="Add a genre"
            />
            <button 
              type="button" 
              className="add-button"
              onClick={handleAddGenre}
            >
              Add
            </button>
          </div>
          <div className="tags-container">
            {movie.genres.map((genre, index) => (
              <div key={index} className="tag">
                {genre}
                <button 
                  type="button" 
                  className="remove-tag" 
                  onClick={() => handleRemoveGenre(genre)}
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        </div>
        
        <div className="form-group">
          <label htmlFor="categories">Categories</label>
          <div className="input-with-button">
            <input
              type="text"
              id="categories"
              value={categoryInput}
              onChange={(e) => setCategoryInput(e.target.value)}
              placeholder="Add a category"
            />
            <button 
              type="button" 
              className="add-button"
              onClick={handleAddCategory}
            >
              Add
            </button>
          </div>
          <div className="tags-container">
            {movie.category.map((category, index) => (
              <div key={index} className="tag">
                {category}
                <button 
                  type="button" 
                  className="remove-tag" 
                  onClick={() => handleRemoveCategory(category)}
                >
                  ×
                </button>
              </div>
            ))}
          </div>
        </div>
        
        <div className="form-actions">
          <button 
            type="button" 
            className="cancel-button"
            onClick={() => navigate('/')}
          >
            Cancel
          </button>
          <button 
            type="submit" 
            className="submit-button"
            disabled={loading}
          >
            {loading ? 'Saving...' : 'Save Movie'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default NewMovie;