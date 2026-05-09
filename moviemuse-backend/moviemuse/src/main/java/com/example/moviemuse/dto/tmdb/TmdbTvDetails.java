package com.example.moviemuse.dto.tmdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbTvDetails {
    private Long id;
    private String name;
    @JsonProperty("original_name")
    private String originalName;
    private String overview;
    @JsonProperty("poster_path")
    private String posterPath;
    @JsonProperty("release_date")
    private String releaseDate;
    private List<TmdbGenre> genres;
    @JsonProperty("number_of_episodes")
    private Integer numberOfEpisodes;
}
