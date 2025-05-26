package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.request.CreateGenreRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GenreController {
    private final GenreService genreService;

    @GetMapping("admin/genres/get-all")
    public ResponseEntity<ResponseObject> getAllGenresAdmin() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @GetMapping("genres/get-all")
    public ResponseEntity<ResponseObject> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @PostMapping("admin/genres")
    public ResponseEntity<ResponseObject> createGenre(@RequestBody CreateGenreRequest request) {
        return ResponseEntity.ok(genreService.createGenre(request));
    }
}