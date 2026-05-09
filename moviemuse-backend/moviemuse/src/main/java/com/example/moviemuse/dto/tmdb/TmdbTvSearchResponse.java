package com.example.moviemuse.dto.tmdb;

import java.util.List;

import lombok.Data;

@Data
public class TmdbTvSearchResponse {
    private List<TmdbTvShowResult> results;
}
