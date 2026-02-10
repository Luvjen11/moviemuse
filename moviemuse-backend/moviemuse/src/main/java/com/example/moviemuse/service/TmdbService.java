package com.example.moviemuse.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.moviemuse.dto.tmdb.TmdbMovieDetails;
import com.example.moviemuse.dto.tmdb.TmdbSearchResponse;

@Service
public class TmdbService {
    
    private final WebClient webClient;

    @Value("${tmdb.api.key}")
    private String apiKey;

    public TmdbService(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("https://api.themoviedb.org/3")
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .build();
    }

    
    public TmdbSearchResponse searchMovies(String query) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("query", query)
                .queryParam("include_adult", "false")
                .build()
            )
            .retrieve()
            .bodyToMono(TmdbSearchResponse.class)
            .block();
    }

    public TmdbMovieDetails getMovieDetails(Long tmdbId) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/movie/{id}")
                .queryParam("api_key", apiKey)
                .build(tmdbId)
            )
            .retrieve()
            .bodyToMono(TmdbMovieDetails.class)
            .block();
    }


}
