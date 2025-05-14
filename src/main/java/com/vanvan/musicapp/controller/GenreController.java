package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.request.CreateGenreRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/genres")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/get-all")
    public ResponseEntity<ResponseObject> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createGenre(@RequestBody CreateGenreRequest request) {
        return ResponseEntity.ok(genreService.createGenre(request));
    }
}