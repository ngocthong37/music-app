package com.vanvan.musicapp.controller;

import com.vanvan.musicapp.entity.Favorite;
import com.vanvan.musicapp.request.FavoriteRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/add")
    public ResponseEntity<ResponseObject> addFavorite(@RequestBody FavoriteRequest request) {
        try {
            return ResponseEntity.ok(favoriteService.addFavorite(request.getUserId(), request.getSongId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<ResponseObject> removeFavorite(@RequestBody FavoriteRequest request) {
        try {
            favoriteService.removeFavorite(request.getUserId(), request.getSongId());
            return ResponseEntity.ok(new ResponseObject("success", "Đã xóa bài hát khỏi danh sách yêu thích", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    @GetMapping("/get-by-user-id/{userId}")
    public ResponseEntity<ResponseObject> getByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(favoriteService.getFavoritesByUserId(userId));
    }

    @GetMapping("/top-songs")
    public ResponseEntity<ResponseObject> getTop10FavoriteSongs() {
        ResponseObject response = favoriteService.getTop10FavoriteSongs();
        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
    }

    @GetMapping("/top-genres")
    public ResponseEntity<ResponseObject> getTop10FavoriteGenres() {
        ResponseObject response = favoriteService.getTop10FavoriteGenres();
        return ResponseEntity.status(response.getStatus().equals("success") ? 200 : 400).body(response);
    }

}