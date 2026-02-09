package com.example.moviemuse.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.moviemuse.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>{
    Optional<Movie> findByExternalIdAndSource(String externalId, String source);
}
