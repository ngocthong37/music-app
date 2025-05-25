package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.repository.ArtistRepository;
import com.vanvan.musicapp.request.CreateArtistRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ArtistController {

    private final ArtistService artistService;
    private final ArtistRepository artistRepository;

    @GetMapping("artists/get-all")
    public ResponseEntity<ResponseObject> getAllArtists() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @GetMapping("admin/artists/get-all")
    public ResponseEntity<ResponseObject> getAllArtistsAdmin() {
        return ResponseEntity.ok(artistService.getAllArtists());
    }

    @PostMapping("admin/artists/upload-artist-avatar")
    public String uploadImageSong(@RequestParam("namePath") String namePath, @RequestParam("file") MultipartFile file,
                                  @RequestParam("artistId") Integer artistId) {
        return artistService.uploadImage(file, namePath, artistId);
    }

    @PostMapping("admin/artists")
    public ResponseEntity<ResponseObject> createArtist(@RequestBody CreateArtistRequest request) {
        return ResponseEntity.ok(artistService.createArtist(request));
    }

    @PutMapping("admin/artists/{id}")
    public ResponseEntity<ResponseObject> updateArtist(@PathVariable Integer id, @RequestBody CreateArtistRequest request) {
        return ResponseEntity.ok(artistService.updateArtist(id, request));
    }

    @DeleteMapping("admin/artists/{id}")
    public ResponseEntity<ResponseObject> deleteArtist(@PathVariable Integer id) {
        return ResponseEntity.ok(artistService.deleteArtist(id));
    }

}