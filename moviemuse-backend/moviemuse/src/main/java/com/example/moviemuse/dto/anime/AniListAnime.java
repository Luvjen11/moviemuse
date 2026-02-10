package com.example.moviemuse.dto.anime;

import lombok.Data;
import java.util.List;

@Data
public class AniListAnime {
    private Integer id;
    private AniListTitle title;
    private Integer episodes;
    private AniListCoverImage coverImage;
    private List<String> genres;
    private String description;
}
