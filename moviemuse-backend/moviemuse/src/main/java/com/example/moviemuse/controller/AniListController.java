package com.example.moviemuse.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.moviemuse.service.AniListService;
import com.example.moviemuse.dto.anime.AniListAnime;
import com.example.moviemuse.dto.anime.AniListResponse;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/moviemuse/anilist/search")
@CrossOrigin("*")
public class AniListController {
    
    private final AniListService aniListService;

    @Autowired
    public AniListController(AniListService aniListService) {
        this.aniListService = aniListService;
    }

    @GetMapping("/anime")
    public List<AniListAnime> search(@RequestParam String query) {
        AniListResponse response = aniListService.searchAnime(query);
        return response.getData().getPage().getMedia();
    }
    
}
