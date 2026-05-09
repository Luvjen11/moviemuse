package com.example.moviemuse.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import com.example.moviemuse.service.RecommenderService;
import com.example.moviemuse.dto.recommendation.RecommendationResponseDto;

@RestController
@RequestMapping("/moviemuse")
@CrossOrigin("*")
public class RecommendationController {
    
    @Autowired
    private RecommenderService recommenderService;

    @GetMapping("/recommendations/{movieId}")
    public ResponseEntity<RecommendationResponseDto> recommend(@PathVariable Long movieId, @RequestParam(defaultValue = "10") int topN, @RequestParam(defaultValue = "true") boolean excludeTitle) {
        return ResponseEntity.ok(recommenderService.recommend(movieId, topN, excludeTitle));
    }
}
