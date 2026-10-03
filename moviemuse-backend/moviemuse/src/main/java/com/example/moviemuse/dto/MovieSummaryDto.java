package com.example.moviemuse.dto;

import java.util.List;

import com.example.moviemuse.model.ContentType;
import com.example.moviemuse.model.Movie;

public record MovieSummaryDto(
        Long id,
        String title,
        int episodes,
        String imageURL,
        String externalId,
        String source,
        List<String> genres,
        String status,
        String description,
        Boolean inWatchlist,
        ContentType type) {

    public static MovieSummaryDto from(Movie movie) {
        return new MovieSummaryDto(
                movie.getId(),
                movie.getTitle(),
                movie.getEpisodes(),
                movie.getImageURL(),
                movie.getExternalId(),
                movie.getSource(),
                movie.getGenres(),
                movie.getStatus(),
                movie.getDescription(),
                movie.getInWatchlist(),
                movie.getType());
    }
}
