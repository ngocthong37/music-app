package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getRecommendedSongs(@PathVariable Integer userId) {
        ResponseObject response = recommendationService.getRecommendedSongs(userId);
        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
    }
}