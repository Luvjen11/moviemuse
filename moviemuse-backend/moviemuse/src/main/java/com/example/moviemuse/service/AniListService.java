package com.example.moviemuse.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.util.Map;

import com.example.moviemuse.dto.anime.AniListResponse; // ensure this import exists

@Service
public class AniListService {

    private final WebClient webClient;

    public AniListService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("https://graphql.anilist.co")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .build();
    }

    public AniListResponse searchAnime(String query) { // changed return type to AniListResponse

        Map<String, Object> body = Map.of(
            "query", """
                        query ($search: String) {
                            Page(perPage: 10) {
                                media(search: $search, type: ANIME) {
                                  id
                                  title {
                                    english
                                    romaji
                                  }
                                  episodes
                                  coverImage {
                                    large
                                  }
                                  genres
                                }
                            }
                        }
                        """,
            "variables", Map.of("search", query)
        );

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(AniListResponse.class) // return AniListResponse
                .block();
    }
}