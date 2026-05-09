package com.example.moviemuse.service;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.moviemuse.dto.anime.AniListAnime;
import com.example.moviemuse.dto.anime.AniListByIdResponse;
import com.example.moviemuse.dto.anime.AniListResponse;

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

    public AniListResponse searchAnime(String query) {
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
                                  description(asHtml: false)
                                }
                            }
                        }
                        """,
            "variables", Map.of("search", query)
        );

        return webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(AniListResponse.class)
                .block();
    }

    /**
     * Full anime details by AniList media id (GraphQL: Media(id, type: ANIME)).
     */
    public AniListAnime getAnimeById(int anilistMediaId) {
        Map<String, Object> body = Map.of(
                "query", """
                        query ($id: Int) {
                            Media(id: $id, type: ANIME) {
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
                                description(asHtml: false)
                            }
                        }
                        """,
                "variables", Map.of("id", anilistMediaId));

        AniListByIdResponse response = webClient.post()
                .bodyValue(body)
                .retrieve()
                .bodyToMono(AniListByIdResponse.class)
                .block();

        if (response == null || response.getData() == null) {
            return null;
        }
        return response.getData().getMedia();
    }
}
