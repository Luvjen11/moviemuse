package com.example.moviemuse.dto.tmdb;

import lombok.Data;

@Data
public class ExternalMovieDto {

    private Long externalId;
    private String title;
    private String description;
    private String imageURL;
    private String source; 
    private String type;   
    private String releaseDate;
    
}
