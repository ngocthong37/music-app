package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Favorite;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.FavoriteRepository;
import com.vanvan.musicapp.repository.SongRepository;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.response.FavoriteResponse;
import com.vanvan.musicapp.response.GenreResponse;
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

    public ResponseObject addFavorite(Integer userId, Integer songId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));

            if (favoriteRepository.existsByUserAndSong(user, song)) {
                return new ResponseObject("error", "Bài hát đã có trong danh sách yêu thích", null);
            }

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setSong(song);
            favorite.setCreatedAt(new Date());

            favoriteRepository.save(favorite);

            SongResponse songResponse = new SongResponse(
                    song.getId(),
                    song.getTitle(),
                    song.getArtist() != null ? song.getArtist().getId() : null,
                    song.getArtist() != null ? song.getArtist().getName() : null,
                    song.getDuration(),
                    song.getFileUrl(),
                    song.getImageUrl(),
                    song.getGenre().getId(),
                    song.getGenre().getName()
            );

            return new ResponseObject("success", "Thêm bài hát vào danh sách yêu thích thành công", songResponse);
        } catch (Exception e) {
            return new ResponseObject("error", "Thêm bài hát vào danh sách yêu thích thất bại: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ResponseObject removeFavorite(Integer userId, Integer songId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));

            if (!favoriteRepository.existsByUserAndSong(user, song)) {
                return new ResponseObject("error", "Bài hát không có trong danh sách yêu thích", null);
            }

            favoriteRepository.deleteByUserAndSong(user, song);

            return new ResponseObject("success", "Xóa bài hát khỏi danh sách yêu thích thành công", null);
        } catch (Exception e) {
            return new ResponseObject("error", "Xóa bài hát khỏi danh sách yêu thích thất bại: " + e.getMessage(), null);
        }
    }

    public ResponseObject getFavoritesByUserId(Integer userId) {
        try {
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            List<Favorite> favorites = favoriteRepository.findByUserId(userId);
            List<SongResponse> songResponses = favorites.stream()
                    .map(favorite -> {
                        Song song = favorite.getSong();
                        return new SongResponse(
                                song.getId(),
                                song.getTitle(),
                                song.getArtist() != null ? song.getArtist().getId() : null,
                                song.getArtist() != null ? song.getArtist().getName() : null,
                                song.getDuration(),
                                song.getFileUrl(),
                                song.getImageUrl(),
                                song.getGenre().getId(),
                                song.getGenre().getName()
                        );
                    })
                    .collect(Collectors.toList());

            FavoriteResponse response = new FavoriteResponse();
            response.setId(userId);
            response.setSongs(songResponses);

            return new ResponseObject("success", "Lấy danh sách yêu thích thành công", response);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy danh sách yêu thích thất bại: " + e.getMessage(), null);
        }
    }

    public ResponseObject getTop10FavoriteSongs() {
        try {
            List<Object[]> topSongs = favoriteRepository.findTopFavoriteSongs();
            List<SongResponse> responses = topSongs.stream()
                    .limit(10)
                    .map(result -> {
                        Integer songId = (Integer) result[0];
                        Long favoriteCount = (Long) result[1];
                        Song song = songRepository.findById(songId)
                                .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));
                        return new SongResponse(
                                song.getId(),
                                song.getTitle(),
                                song.getArtist() != null ? song.getArtist().getId() : null,
                                song.getArtist() != null ? song.getArtist().getName() : null,
                                song.getDuration(),
                                song.getFileUrl(),
                                song.getImageUrl(),
                                song.getGenre().getId(),
                                song.getGenre().getName(),
                                null,
                                favoriteCount
                        );
                    })
                    .collect(Collectors.toList());
            return new ResponseObject("success", "Lấy top 10 bài hát được yêu thích nhất thành công", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy top 10 bài hát được yêu thích thất bại: " + e.getMessage(), null);
        }
    }

    public ResponseObject getTop10FavoriteGenres() {
        try {
            List<Object[]> topGenres = favoriteRepository.findTopFavoriteGenres();
            List<GenreResponse> responses = topGenres.stream()
                    .limit(10)
                    .map(result -> {
                        Integer genreId = (Integer) result[0];
                        String genreName = (String) result[1];
                        Long favoriteCount = (Long) result[2];
                        return new GenreResponse(genreId, genreName, favoriteCount);
                    })
                    .collect(Collectors.toList());
            return new ResponseObject("success", "Lấy top 10 thể loại được yêu thích nhất thành công", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy top 10 thể loại được yêu thích thất bại: " + e.getMessage(), null);
        }
    }
}