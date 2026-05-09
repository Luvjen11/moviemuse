package com.example.moviemuse.dto.recommendation;

import java.util.List;

import lombok.Data;

@Data
public class RecommendationResponseDto {
    private String queryTitle;
    private String model;
    private List<RecommendationItemDto> recommendations;
}
