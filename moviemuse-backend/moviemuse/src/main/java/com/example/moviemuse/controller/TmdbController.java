package com.example.moviemuse.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.moviemuse.service.MovieService;
import com.example.moviemuse.service.TmdbService;
import com.example.moviemuse.dto.tmdb.TmdbSearchResponse;
import com.example.moviemuse.model.Movie;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.moviemuse.dto.tmdb.TmdbTvSearchResponse;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/moviemuse/tmdb")
@CrossOrigin("*")
public class TmdbController {
    
    private final TmdbService tmdbService;
    private final MovieService movieService;

    public TmdbController(TmdbService tmdbService, MovieService movieService) {
        this.tmdbService = tmdbService;
        this.movieService = movieService;
    }

    @GetMapping("/search/movie")
    public ResponseEntity<TmdbSearchResponse> search(@RequestParam String query) {
        return ResponseEntity.ok(tmdbService.searchMovies(query));
    }

    @PostMapping("/import/{tmdbId}")
    public ResponseEntity<Movie> importMovie(@PathVariable Long tmdbId) {
        var details = tmdbService.getMovieDetails(tmdbId);
        var saved = movieService.importFromTmdbMovie(details);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/search/tv")
    public ResponseEntity<TmdbTvSearchResponse> searchTv(@RequestParam String query) {
        return ResponseEntity.ok(tmdbService.searchTvShows(query));
    }

    @PostMapping("/import/tv/{tmdbId}")
    public ResponseEntity<Movie> importTvShow(@PathVariable Long tmdbId) {
        var details = tmdbService.getTvShowDetails(tmdbId);
        var saved = movieService.importFromTmdbTvShow(details);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
