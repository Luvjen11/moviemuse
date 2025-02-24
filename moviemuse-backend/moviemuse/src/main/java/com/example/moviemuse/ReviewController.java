package com.example.moviemuse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moviemuse/review")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;

    //create review
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody String body) {

        return new ResponseEntity<Review>(reviewService.createReview(null), HttpStatus.CREATED);
    }

    //update review
    @PutMapping
    public ResponseEntity<Review> updateReview(@PathVariable Long id ,@RequestBody Review review) {

        return new ResponseEntity<Review>(reviewService.updateReview(id, review), HttpStatus.ACCEPTED);
    }

}
