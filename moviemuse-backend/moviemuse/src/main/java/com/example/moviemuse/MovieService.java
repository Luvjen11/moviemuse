package com.example.moviemuse;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MovieService {
    
    @Autowired
    private MovieRepository movieRepository;

    // Find all movies
    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    // Create a movie
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }
    
    // Create a movie with file upload
    public Movie createMovieWithFile(MovieDTO movieDTO) throws IOException {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setEpisodes(movieDTO.getEpisodes());
        movie.setCategory(movieDTO.getCategory());
        movie.setGenres(movieDTO.getGenres());
        
        // Handle the poster image
        if (movieDTO.getPosterFile() != null && !movieDTO.getPosterFile().isEmpty()) {
            // Convert MultipartFile to base64 string
            String base64Image = Base64.getEncoder().encodeToString(movieDTO.getPosterFile().getBytes());
            // Add data URI prefix for proper display in browser
            movie.setPoster("data:" + movieDTO.getPosterFile().getContentType() + ";base64," + base64Image);
        } else if (movieDTO.getPoster() != null && !movieDTO.getPoster().isEmpty()) {
            // Use the provided URL or base64 string
            movie.setPoster(movieDTO.getPoster());
        }
        
        return movieRepository.save(movie);
    }

    // Find all movies by category
    public List<Movie> findMovieByCategory(String category) {
        return movieRepository.findMovieByCategory(category);
    }
    
    // Find movie by ID
    public Movie findMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    // Delete movie by ID
    public void deleteMovie(Long id) {
        // Check if movie exists before deleting
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found with id: " + id);
        }
        // Delete associated reviews first to maintain referential integrity
        movieRepository.deleteById(id);
    }
}
