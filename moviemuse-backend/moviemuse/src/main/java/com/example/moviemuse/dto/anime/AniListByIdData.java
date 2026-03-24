package com.example.moviemuse.dto.anime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AniListByIdData {

    @JsonProperty("Media")
    private AniListAnime media;
}
