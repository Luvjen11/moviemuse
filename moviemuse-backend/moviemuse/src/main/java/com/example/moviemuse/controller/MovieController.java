package com.example.moviemuse.controller;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.moviemuse.model.Movie;
import com.example.moviemuse.model.ContentType;
import com.example.moviemuse.service.MovieService;
import com.example.moviemuse.dto.MovieDTO;
import com.example.moviemuse.dto.anime.AniListAnime;

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

    // import Anilist anime/movie
    @PostMapping("/anilist/import")
    public ResponseEntity<Movie> importAniListMovie(@RequestBody AniListAnime aniListAnime) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.importFromAniList(aniListAnime));
    }

    // Create a new movie (JSON only)
    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        if (movie.getType() == null) {
            movie.setType(ContentType.MOVIE);
        }
        Movie createdMovie = movieService.createMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    // Create a new movie with file upload
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Movie> createMovieWithFile(
            @RequestParam("title") String title,
            @RequestParam("episodes") String episodes,
            @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
            @RequestParam(value = "poster", required = false) String poster,
            @RequestParam(value = "genres", required = false) List<String> genres,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status) {
        
        try {
            
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.setTitle(title);
            movieDTO.setEpisodes(Integer.parseInt(episodes));
            movieDTO.setPosterFile(posterFile);
            movieDTO.setPoster(poster);
            movieDTO.setGenres(genres);
            movieDTO.setType(type);
            movieDTO.setStatus(status);

            
            Movie createdMovie = movieService.createMovieWithFile(movieDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
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

    // // Find movie by category
    // @GetMapping("/category/{category}")
    // public ResponseEntity<List<Movie>> getAllMoviesByCategory(@PathVariable String category) {
    //     List<Movie> movieCategory = movieService.findMovieByCategory(category);
    //     return new ResponseEntity<>(movieCategory, HttpStatus.OK);
    // }

    // Delete movie by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

}
