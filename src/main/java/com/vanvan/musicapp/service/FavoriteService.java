package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Favorite;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.FavoriteRepository;
import com.vanvan.musicapp.repository.SongRepository;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.response.FavoriteResponse;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.response.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public Favorite addFavorite(Integer userId, Integer songId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));

        if (favoriteRepository.existsByUserAndSong(user, song)) {
            throw new RuntimeException("Bài hát đã có trong danh sách yêu thích");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setSong(song);
        favorite.setCreatedAt(new Date());

        return favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Integer userId, Integer songId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));

        if (!favoriteRepository.existsByUserAndSong(user, song)) {
            throw new RuntimeException("Bài hát không có trong danh sách yêu thích");
        }

        favoriteRepository.deleteByUserAndSong(user, song);
    }

    public ResponseObject getFavoritesByUserId(Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        try {
            List<Favorite> favorites = favoriteRepository.findByUserId(userId);
            List<SongResponse> songResponses = favorites.stream()
                    .map(favorite -> {
                        Song song = favorite.getSong();
                        return new SongResponse(
                                song.getId(),
                                song.getTitle(),
                                song.getArtist().getId(),
                                song.getDuration(),
                                song.getFileUrl(),
                                song.getImageUrl(),
                                song.getGenre().getId()
                        );
                    })
                    .collect(Collectors.toList());

            FavoriteResponse response = new FavoriteResponse();
            response.setId(userId); // Gán userId làm id của FavoriteResponse
            response.setSongs(songResponses);

            return new ResponseObject("success", "FavoriteResponse found", response);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy danh sách yêu thích thất bại: " + e.getMessage(), null);
        }
    }
}