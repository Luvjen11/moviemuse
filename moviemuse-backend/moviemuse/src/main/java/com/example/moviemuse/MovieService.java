package com.example.moviemuse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    
    @Autowired
    private MovieRepository movieRepository;

    //find all movies
    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    //find all movies by category
    public List<Movie> findMovieByCategory(String category) {

        return movieRepository.findMovieByCategory(category);
    }
}
