package com.example.moviemuse.dto.tmdb;

import java.util.List;
import lombok.Data;

@Data
public class TmdbSearchResponse {
    private List<TmdbMovieResult> results;
}
