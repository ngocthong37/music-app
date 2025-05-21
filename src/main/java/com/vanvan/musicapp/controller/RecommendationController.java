package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseObject getRecommendations(@PathVariable Integer userId, @RequestParam(defaultValue = "10") int limit) {
        return recommendationService.getRecommendations(userId, limit);
    }
}