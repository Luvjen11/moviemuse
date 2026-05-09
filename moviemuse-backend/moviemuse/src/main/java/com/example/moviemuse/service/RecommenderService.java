package com.example.moviemuse.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.moviemuse.dto.recommendation.RecommendationRequestDto;
import com.example.moviemuse.dto.recommendation.RecommendationResponseDto;
import com.example.moviemuse.repository.MovieRepository;

@Service
public class RecommenderService {

    private final MovieRepository movieRepository;
    private final WebClient recommenderClient;

    public RecommenderService(
            MovieRepository movieRepository,
            WebClient.Builder webClientBuilder,
            @Value("${recommender.base.url:http://localhost:8001}") String recommenderBaseUrl) {
        this.movieRepository = movieRepository;
        this.recommenderClient = webClientBuilder.baseUrl(recommenderBaseUrl).build();
    }

    public RecommendationResponseDto recommend(Long movieId, int topN, boolean excludeTitle) {
        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        var request = new RecommendationRequestDto();
        request.setTitle(movie.getTitle());
        request.setTopN(topN);
        request.setExcludeTitle(excludeTitle);

        return recommenderClient
                .post()
                .uri("/recommend")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RecommendationResponseDto.class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }
}