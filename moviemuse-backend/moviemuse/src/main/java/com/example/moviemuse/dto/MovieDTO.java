package com.example.moviemuse.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {

    private String title;
    private int episodes;

    private MultipartFile posterFile;
    private String poster; // imageURL

    private List<String> genres;     
    private String type;              
    private String status; 
    private String description;
}