package com.example.moviemuse;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/moviemuse")
@CrossOrigin("*")
public class MovieController {
    
    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.findAllMovies();
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    // Create a new movie (JSON only)
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }
    
    // Create a new movie with file upload
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Movie> createMovieWithFile(
            @RequestPart("title") String title,
            @RequestPart("episodes") String episodes,
            @RequestPart(value = "posterFile", required = false) MultipartFile posterFile,
            @RequestPart(value = "poster", required = false) String poster,
            @RequestPart(value = "genres", required = false) List<String> genres,
            @RequestPart(value = "category", required = false) List<String> category) {
        
        try {
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setTitle(title);
            movieDTO.setEpisodes(Integer.parseInt(episodes));
            movieDTO.setPosterFile(posterFile);
            movieDTO.setPoster(poster);
            movieDTO.setGenres(genres);
            movieDTO.setCategory(category);
            
            Movie createdMovie = movieService.createMovieWithFile(movieDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get movie by ID
    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        try {
            Movie movie = movieService.findMovieById(id);
            return ResponseEntity.ok(movie);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Find movie by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Movie>> getAllMoviesByCategory(@PathVariable String category) {
        List<Movie> movieCategory = movieService.findMovieByCategory(category);
        return new ResponseEntity<>(movieCategory, HttpStatus.OK);
    }
}
