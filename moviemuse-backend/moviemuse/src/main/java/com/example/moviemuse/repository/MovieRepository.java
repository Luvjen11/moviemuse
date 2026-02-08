package com.example.moviemuse.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.moviemuse.model.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>{

}
