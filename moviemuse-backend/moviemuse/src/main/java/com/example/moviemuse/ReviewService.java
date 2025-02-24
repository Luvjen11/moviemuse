package com.example.moviemuse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    
    @Autowired
    public ReviewRepository reviewRepository;

    //create a new review
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    //update
    public Review updateReview(Long id, Review review) {

        Review existingReview = reviewRepository.findById(id).orElseThrow(()-> new RuntimeException("Review not found"));

        existingReview.setBody(review.getBody());

        return reviewRepository.save(existingReview);
    }
}
