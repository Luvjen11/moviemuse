package com.example.moviemuse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moviemuse")
public class MovieController {
    
    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.findAllMovies();
        System.out.println(movies);
        return new ResponseEntity<List<Movie>>(movieService.findAllMovies(), HttpStatus.OK);
    }

    //find movie by category
    @GetMapping("/{category}")
    public ResponseEntity<List<Movie>> getAllMoviesByCategory(@PathVariable String category) {

        List<Movie> movieCategory = movieService.findMovieByCategory(category);
        System.out.println(movieCategory);

        return new ResponseEntity<List<Movie>>(movieService.findMovieByCategory(category), HttpStatus.OK);
    }
}
