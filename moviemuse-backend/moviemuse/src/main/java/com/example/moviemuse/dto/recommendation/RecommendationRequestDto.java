package com.example.moviemuse.dto.recommendation;

import lombok.Data;

@Data
public class RecommendationRequestDto {
    private String title;
    private int topN;
    private boolean excludeTitle;
}
