package com.example.moviemuse.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TmdbTvShowResult {
        private Long id;
        private String name;
        @JsonProperty("original_name")
        private String originalName;
        private String overview;
        @JsonProperty("poster_path")
        private String posterPath;
        @JsonProperty("release_date")
        private String releaseDate;
        @JsonProperty("number_of_episodes")
        private Integer numberOfEpisodes;
}
