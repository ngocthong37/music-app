package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.request.PlaylistCreateRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.PlaylistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/playlists")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createPlaylist(@Valid @RequestBody PlaylistCreateRequest request) {
        ResponseObject response = playlistService.createPlaylist(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ResponseObject> getPlaylist(@PathVariable Integer id) {
        ResponseObject response = playlistService.getPlaylist(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getUserPlaylists(@PathVariable Integer userId) {
        ResponseObject response = playlistService.getUserPlaylists(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseObject> updatePlaylist(
            @PathVariable Integer id,
            @Valid @RequestBody PlaylistCreateRequest request) {
        ResponseObject response = playlistService.updatePlaylist(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseObject> deletePlaylist(@PathVariable Integer id) {
        ResponseObject response = playlistService.deletePlaylist(id);
        return ResponseEntity.ok(response);
    }
}
