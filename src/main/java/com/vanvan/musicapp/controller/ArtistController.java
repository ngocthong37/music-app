package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.request.CreateArtistRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/artists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping("/get-all")
    public ResponseEntity<ResponseObject> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createArtist(@RequestBody CreateArtistRequest request) {
        return ResponseEntity.ok(artistService.createArtist(request));
    }
}