package com.quocluan.kdmaylanh.controller;

import com.quocluan.kdmaylanh.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @PostMapping("/reviews/addReview")
    public ResponseEntity<Object> addReview(@RequestBody String json) {
        return reviewService.addReview(json);
    }

}
