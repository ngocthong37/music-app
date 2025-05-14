package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.request.CreateAlbumRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createAlbum(@RequestBody CreateAlbumRequest request) {
        ResponseObject response = albumService.createAlbum(request);
        if ("success".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<ResponseObject> getAllAlbums() {
        ResponseObject response = albumService.getAllAlbums();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<ResponseObject> getAlbumById(@PathVariable Integer id) {
        return ResponseEntity.ok(albumService.getAlbumById(id));
    }

    @PostMapping("/upload-image-cover")
    public String uploadImageSong(@RequestParam("namePath") String namePath, @RequestParam("file") MultipartFile file,
                                  @RequestParam("albumId") Integer albumId) {
        return albumService.uploadImage(file, namePath, albumId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateAlbum(@PathVariable Integer id, @RequestBody CreateAlbumRequest request) {
        ResponseObject response = albumService.updateAlbum(id, request);
        if ("success".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteAlbum(@PathVariable Integer id) {
        ResponseObject response = albumService.deleteAlbum(id);
        if ("success".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}