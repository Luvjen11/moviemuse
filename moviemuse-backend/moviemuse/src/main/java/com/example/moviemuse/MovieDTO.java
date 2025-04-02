package com.example.moviemuse;

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
    private String poster; // For URL or base64 string
    private List<String> category;
    private List<String> genres;
}