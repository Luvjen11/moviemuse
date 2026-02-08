import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './NewMovie.css';
import { createMovie, searchAnime, importAnime } from '../services/api';

const NewMovie = () => {
  const navigate = useNavigate();
  const [anilistQuery, setAnilistQuery] = useState('');
  const [anilistResults, setAnilistResults] = useState([]);
  const [anilistSearching, setAnilistSearching] = useState(false);
  const [anilistError, setAnilistError] = useState('');
  const [importingId, setImportingId] = useState(null);
  const [movie, setMovie] = useState({
    title: '',
    episodes: 0,
    poster: '',
    genres: [],
    category: [],
    type: 'MOVIE',
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

  const handleAnilistSearch = async () => {
    setAnilistError('');
    setAnilistResults([]);
    if (!anilistQuery.trim()) return;
    setAnilistSearching(true);
    try {
      const results = await searchAnime(anilistQuery);
      setAnilistResults(Array.isArray(results) ? results : []);
    } catch (err) {
      setAnilistError(err.message || 'Search failed');
      setAnilistResults([]);
    } finally {
      setAnilistSearching(false);
    }
  };

  const handleImportAnime = async (anime) => {
    setAnilistError('');
    setImportingId(anime?.id ?? null);
    try {
      await importAnime(anime);
      navigate('/');
    } catch (err) {
      setAnilistError(err.message || 'Import failed');
    } finally {
      setImportingId(null);
    }
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
        formData.append('type', movie.type);
        
        // Add the file if it exists
        if (posterFile) {
            formData.append('posterFile', posterFile);
        } else if (movie.imageURL) {
            // If no file but URL exists, send the URL
            formData.append('imageURL', movie.imageURL);
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

  const anilistTitle = (a) =>
    a?.title?.english || a?.title?.romaji || 'Unknown';

  return (
    <div className="new-movie-container">
      <h2 className="new-movie-title">Add New Movie</h2>

      <section className="anilist-import-section">
        <h3 className="anilist-import-heading">Import from AniList</h3>
        <div className="anilist-search-row">
          <input
            type="text"
            className="anilist-search-input"
            placeholder="Search anime by title..."
            value={anilistQuery}
            onChange={(e) => setAnilistQuery(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && (e.preventDefault(), handleAnilistSearch())}
          />
          <button
            type="button"
            className="anilist-search-button"
            onClick={handleAnilistSearch}
            disabled={anilistSearching}
          >
            {anilistSearching ? 'Searching...' : 'Search'}
          </button>
        </div>
        {anilistError && <div className="error-message">{anilistError}</div>}
        {anilistResults.length > 0 && (
          <ul className="anilist-results-list">
            {anilistResults.map((a) => (
              <li key={a.id} className="anilist-result-item">
                <div className="anilist-result-info">
                  {a.coverImage?.large && (
                    <img src={a.coverImage.large} alt="" className="anilist-result-poster" />
                  )}
                  <div>
                    <span className="anilist-result-title">{anilistTitle(a)}</span>
                    {a.episodes != null && (
                      <span className="anilist-result-meta"> · {a.episodes} ep</span>
                    )}
                  </div>
                </div>
                <button
                  type="button"
                  className="anilist-import-button"
                  onClick={() => handleImportAnime(a)}
                  disabled={importingId !== null}
                >
                  {importingId === a.id ? 'Importing...' : 'Import'}
                </button>
              </li>
            ))}
          </ul>
        )}
      </section>

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
          <label htmlFor="type">Type</label>
          <select
            id="type"
            name="type"
            value={movie.type}
            onChange={handleChange}
          >
            <option value="MOVIE">Movie</option>
            <option value="ANIME">Anime</option>
            <option value="KDRAMA">K-Drama</option>
          </select>
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
            id="imageURL"
            name="imageURL"
            value={ movie.imageURL}
            onChange={handleChange}
            placeholder="Enter poster image URL"
          />
          
          {previewUrl && (
            <div className="poster-preview">
              <img src={previewUrl} alt="Poster preview" />
            </div>
          )}
          {!previewUrl && movie.imageURL && (
            <div className="poster-preview">
              <img src={movie.imageURL} alt="Poster preview" />
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