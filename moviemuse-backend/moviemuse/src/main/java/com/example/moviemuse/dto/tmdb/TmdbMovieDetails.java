package com.example.moviemuse.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbMovieDetails {
    private Long id;
    private String title;
    private String overview;
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("release_date")
    private String releaseDate;
    private List<TmdbGenre> genres;
}
