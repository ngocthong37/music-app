package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.request.SongRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/songs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
//@PreAuthorize("hasRole('ADMIN')")
public class SongAdminController {

    private final SongService songService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createSong(
            @RequestParam("title") String title,
            @RequestParam("artistId") Integer artistId,
            @RequestParam("genreId") Integer genreId,
            @RequestParam("duration") int duration,
            @RequestParam(value = "lyrics", required = false) String lyrics,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(songService.createSong(title, artistId, genreId, duration, lyrics, file));
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateSong(@PathVariable Integer id, @RequestBody SongRequest request) {
        return ResponseEntity.ok(songService.updateSong(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable Integer id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity<ResponseObject>  getAllSongs() {
        ResponseObject response = songService.getAllSongs();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-image-song")
    public String uploadImageSong(@RequestParam("namePath") String namePath, @RequestParam("file") MultipartFile file,
                                         @RequestParam("songId") Integer songId) {
        return songService.uploadImage(file, namePath, songId);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchSongs(@RequestParam String keyword) {
        return ResponseEntity.ok(songService.searchSongs(keyword));
    }

}
