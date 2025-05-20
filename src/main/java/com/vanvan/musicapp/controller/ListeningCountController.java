package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.request.FavoriteRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.ListeningCountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/listening-counts")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class ListeningCountController {

    private final ListeningCountService listeningCountService;

    @PostMapping
    public ResponseEntity<ResponseObject> incrementListeningCount(@RequestBody FavoriteRequest request) {
        ResponseObject response = listeningCountService.incrementListeningCount(request.getUserId(), request.getSongId());
        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseObject> getListeningCountsByUser(@PathVariable Integer userId) {
        ResponseObject response = listeningCountService.getListeningCountsByUserId(userId);
        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
    }


//    @GetMapping("/top")
//    public ResponseEntity<ResponseObject> getTop20SongsByListenCount() {
//        ResponseObject response = listeningCountService.getTop20SongsByListenCount();
//        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
//    }

    @GetMapping("/top-songs")
    public ResponseEntity<ResponseObject> getTop10SongsByListenCount() {
        ResponseObject response = listeningCountService.getTop10SongsByListenCount();
        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
    }

    @GetMapping("/top-artists")
    public ResponseEntity<ResponseObject> getTop10ArtistsByListenCount() {
        ResponseObject response = listeningCountService.getTop10ArtistsByListenCount();
        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
    }
}