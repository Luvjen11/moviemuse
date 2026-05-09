package com.example.moviemuse.dto.recommendation;

import lombok.Data;

@Data
public class RecommendationItemDto {
    private String title;
    private float score;
    private String imageUrl;
    private Long anilistId;
    
}
