package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/songs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
//@PreAuthorize("hasRole('ADMIN')")
public class SongController {
    private final SongService songService;

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

    @GetMapping("/search")
    public ResponseEntity<ResponseObject> searchSongs(@RequestParam String keyword) {
        return ResponseEntity.ok(songService.searchSongs(keyword));
    }

}
