package com.example.moviemuse.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.moviemuse.model.Movie;
import com.example.moviemuse.model.ContentType;
import com.example.moviemuse.repository.MovieRepository;


import com.example.moviemuse.dto.MovieDTO;
import com.example.moviemuse.dto.anime.AniListAnime;

@Service
public class MovieService {
    
    @Autowired
    private MovieRepository movieRepository;

    // Find all movies
    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    // iport movie from anilist
    public Movie importFromAniList(AniListAnime a) {

        Movie movie = new Movie();

        movie.setTitle(a.getTitle().getEnglish() != null ? a.getTitle().getEnglish() : a.getTitle().getRomaji());
        movie.setEpisodes(a.getEpisodes() != null ? a.getEpisodes() : 0);
        movie.setImageURL(a.getCoverImage().getLarge());
        movie.setGenres(a.getGenres() != null ? a.getGenres() : new ArrayList<>());
        movie.setExternalId(a.getId().toString());
        movie.setSource("ANILIST");
        movie.setType(ContentType.ANIME);
        movie.setStatus("PLANNING");
        movie.setInWatchlist(false);
        movie.setDescription(a.getDescription());
        return movieRepository.save(movie);
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

        // genres
        if (movieDTO.getGenres() != null && !movieDTO.getGenres().isEmpty()) {
            movie.setGenres(movieDTO.getGenres());
        } else {
            movie.setGenres(new ArrayList<>());
        }

        // status
        movie.setStatus(movieDTO.getStatus());
        movie.setDescription(movieDTO.getDescription());
        // manual entry metadata
        movie.setSource("MANUAL");
        movie.setInWatchlist(false); // default
        
        // handle imageURL
        if (movieDTO.getPosterFile() != null && !movieDTO.getPosterFile().isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(movieDTO.getPosterFile().getBytes());
            String dataUri = "data:" + movieDTO.getPosterFile().getContentType() + ";base64," + base64Image;
            movie.setImageURL(dataUri);
        } else if (movieDTO.getPoster() != null && !movieDTO.getPoster().isEmpty()) {
            movie.setImageURL(movieDTO.getPoster());
        }

        // type: default MOVIE if none
        if (movieDTO.getType() != null && !movieDTO.getType().isBlank()) {
            movie.setType(ContentType.valueOf(movieDTO.getType().toUpperCase()));
        } else {
            movie.setType(ContentType.MOVIE);
        }

        return movieRepository.save(movie);
    }



    // // Find all movies by category
    // public List<Movie> findMovieByCategory(String category) {
    //     return movieRepository.findMovieByCategory(category);
    // }
    
    // Find movie by ID
    public Movie findMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    // Delete movie by ID (cascade deletes reviews via Movie entity)
    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        movieRepository.delete(movie);
    }
}
