package com.example.moviemuse.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.moviemuse.dto.BackfillResultDto;
import com.example.moviemuse.dto.MovieDTO;
import com.example.moviemuse.dto.anime.AniListAnime;
import com.example.moviemuse.dto.tmdb.TmdbMovieDetails;
import com.example.moviemuse.model.ContentType;
import com.example.moviemuse.model.Movie;
import com.example.moviemuse.repository.MovieRepository;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final AniListService aniListService;

    @Value("${anilist.backfill.delay-ms:1500}")
    private long anilistBackfillDelayMs;

    @Value("${anilist.backfill.max-description-chars:60000}")
    private int maxDescriptionChars;

    /** MySQL TEXT = 65535 bytes max; stay under with UTF-8 (emoji-heavy text needs byte cap, not just char cap). */
    @Value("${anilist.backfill.max-description-utf8-bytes:65000}")
    private int maxDescriptionUtf8Bytes;

    public MovieService(MovieRepository movieRepository, AniListService aniListService) {
        this.movieRepository = movieRepository;
        this.aniListService = aniListService;
    }

    /**
     * Keeps descriptions under MySQL limits: TEXT is max 65535 bytes UTF-8.
     * Char-only caps (e.g. 60000) can still exceed byte limit with CJK/emoji.
     */
    private String truncateDescription(String description) {
        if (description == null) {
            return null;
        }
        String t = description.trim();
        if (t.length() > maxDescriptionChars) {
            t = t.substring(0, maxDescriptionChars);
        }
        byte[] utf8 = t.getBytes(StandardCharsets.UTF_8);
        if (utf8.length <= maxDescriptionUtf8Bytes) {
            return t;
        }
        int low = 0;
        int high = t.length();
        while (low < high) {
            int mid = (low + high + 1) / 2;
            String sub = t.substring(0, mid);
            if (sub.getBytes(StandardCharsets.UTF_8).length <= maxDescriptionUtf8Bytes) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        return t.substring(0, low);
    }

    private AniListAnime getAnimeByIdWith429Retry(int aniId, BackfillResultDto result) throws InterruptedException {
        final int maxAttempts = 4;
        long backoffMs = 30_000L;
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return aniListService.getAnimeById(aniId);
            } catch (WebClientResponseException e) {
                if (e.getStatusCode().value() == 429 && attempt < maxAttempts) {
                    result.getMessages().add("anilistId=" + aniId + ": rate limited (429), waiting " + (backoffMs / 1000) + "s before retry " + (attempt + 1) + "/" + maxAttempts);
                    Thread.sleep(backoffMs);
                    backoffMs = Math.min(backoffMs * 2, 120_000L);
                    continue;
                }
                throw e;
            }
        }
        return null;
    }

    // Find all movies
    public List<Movie> findAllMovies() {
        return movieRepository.findAll();
    }

    // iport movie from anilist
    public Movie importFromAniList(AniListAnime a) {

        Movie movie = new Movie();

        movie.setTitle(a.getTitle().getEnglish() != null ? a.getTitle().getEnglish() : a.getTitle().getRomaji());
        movie.setEpisodes(a.getEpisodes() != null ? a.getEpisodes() : 0);
        movie.setImageURL(a.getCoverImage().getLarge());
        movie.setGenres(a.getGenres() != null ? a.getGenres() : new ArrayList<>());
        movie.setExternalId(a.getId().toString());
        movie.setSource("ANILIST");
        movie.setType(ContentType.ANIME);
        movie.setStatus("PLANNING");
        movie.setInWatchlist(false);
        movie.setDescription(truncateDescription(a.getDescription()));
        return movieRepository.save(movie);
    }

    // import from TMDB (idempotent: returns existing if already imported)
    public Movie importFromTmdb(TmdbMovieDetails d) {
        String externalId = String.valueOf(d.getId());
        Optional<Movie> existing = movieRepository.findByExternalIdAndSource(externalId, "TMDB");
        if (existing.isPresent()) {
            return existing.get();
        }

        Movie movie = new Movie();
        movie.setTitle(d.getTitle() != null ? d.getTitle() : "Unknown");
        movie.setEpisodes(0);
        // Truncate if DB column is still VARCHAR(255); safe for TEXT too
        String overview = d.getOverview();
        final int maxDesc = 255;
        movie.setDescription(overview == null ? null : (overview.length() > maxDesc ? overview.substring(0, maxDesc) : overview));
        movie.setExternalId(externalId);
        movie.setSource("TMDB");
        movie.setType(ContentType.MOVIE);
        movie.setStatus("PLANNING");
        movie.setInWatchlist(false);

        if (d.getPosterPath() != null && !d.getPosterPath().isBlank()) {
            movie.setImageURL("https://image.tmdb.org/t/p/w500" + d.getPosterPath());
        } else {
            movie.setImageURL(null);
        }

        // genres: never null, no null names (avoids NOT NULL constraint on join table)
        if (d.getGenres() != null && !d.getGenres().isEmpty()) {
            List<String> names = d.getGenres().stream()
                .map(g -> g != null ? g.getName() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            movie.setGenres(names);
        } else {
            movie.setGenres(new ArrayList<>());
        }

        return movieRepository.save(movie);
    }

    // Create a movie
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }
    
    // Create a movie with file upload
    public Movie createMovieWithFile(MovieDTO movieDTO) throws IOException {
        Movie movie = new Movie();
        movie.setTitle(movieDTO.getTitle());
        movie.setEpisodes(movieDTO.getEpisodes());

        // genres
        if (movieDTO.getGenres() != null && !movieDTO.getGenres().isEmpty()) {
            movie.setGenres(movieDTO.getGenres());
        } else {
            movie.setGenres(new ArrayList<>());
        }

        // status
        movie.setStatus(movieDTO.getStatus());
        movie.setDescription(movieDTO.getDescription());
        // manual entry metadata
        movie.setSource("MANUAL");
        movie.setInWatchlist(false); // default
        
        // handle imageURL
        if (movieDTO.getPosterFile() != null && !movieDTO.getPosterFile().isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(movieDTO.getPosterFile().getBytes());
            String dataUri = "data:" + movieDTO.getPosterFile().getContentType() + ";base64," + base64Image;
            movie.setImageURL(dataUri);
        } else if (movieDTO.getPoster() != null && !movieDTO.getPoster().isEmpty()) {
            movie.setImageURL(movieDTO.getPoster());
        }

        // type: default MOVIE if none
        if (movieDTO.getType() != null && !movieDTO.getType().isBlank()) {
            movie.setType(ContentType.valueOf(movieDTO.getType().toUpperCase()));
        } else {
            movie.setType(ContentType.MOVIE);
        }

        return movieRepository.save(movie);
    }


    // Find movie by ID
    public Movie findMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    // Delete movie by ID (cascade deletes reviews via Movie entity)
    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        movieRepository.delete(movie);
    }

    /**
     * For each ANILIST anime with null description, fetch details from AniList by externalId and save description.
     * Throttles requests (see anilist.backfill.delay-ms) and retries on HTTP 429.
     */
    public BackfillResultDto backfillMissingAnilistDescriptions() {
        List<Movie> toFix = movieRepository.findBySourceAndTypeAndDescriptionIsNull("ANILIST", ContentType.ANIME);
        BackfillResultDto result = new BackfillResultDto();

        for (Movie m : toFix) {
            if (m.getExternalId() == null || m.getExternalId().isBlank()) {
                result.setFailed(result.getFailed() + 1);
                result.getMessages().add("movieId=" + m.getId() + ": missing externalId");
                continue;
            }
            try {
                int aniId = Integer.parseInt(m.getExternalId().trim());
                AniListAnime details = getAnimeByIdWith429Retry(aniId, result);
                if (details == null) {
                    result.setSkipped(result.getSkipped() + 1);
                    result.getMessages().add("movieId=" + m.getId() + " anilistId=" + aniId + ": Media not found");
                    continue;
                }
                String desc = details.getDescription();
                if (desc == null || desc.isBlank()) {
                    result.setSkipped(result.getSkipped() + 1);
                    result.getMessages().add("movieId=" + m.getId() + " anilistId=" + aniId + ": empty description from API");
                    continue;
                }
                m.setDescription(truncateDescription(desc));
                movieRepository.save(m);
                result.setUpdated(result.getUpdated() + 1);
                Thread.sleep(anilistBackfillDelayMs);
            } catch (NumberFormatException e) {
                result.setFailed(result.getFailed() + 1);
                result.getMessages().add("movieId=" + m.getId() + ": invalid externalId '" + m.getExternalId() + "'");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                result.getMessages().add("Interrupted after " + result.getUpdated() + " updates");
                break;
            } catch (Exception e) {
                result.setFailed(result.getFailed() + 1);
                result.getMessages().add("movieId=" + m.getId() + ": " + e.getMessage());
            }
        }
        return result;
    }
}
