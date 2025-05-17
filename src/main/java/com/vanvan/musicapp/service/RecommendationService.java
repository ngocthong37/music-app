package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Favorite;
import com.vanvan.musicapp.entity.ListeningCount;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.repository.FavoriteRepository;
import com.vanvan.musicapp.repository.ListeningCountRepository;
import com.vanvan.musicapp.repository.SongRepository;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.response.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final FavoriteRepository favoriteRepository;
    private final ListeningCountRepository listeningCountRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public ResponseObject getRecommendedSongs(Integer userId) {
        try {
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

            // Lấy bài hát yêu thích và đã nghe
            List<Favorite> favorites = favoriteRepository.findByUserId(userId);
            List<ListeningCount> listeningCounts = listeningCountRepository.findByUserId(userId);

            // Tập hợp songId đã nghe hoặc yêu thích để loại bỏ
            Set<Integer> excludeSongIds = new HashSet<>();
            favorites.forEach(f -> excludeSongIds.add(f.getSong().getId()));
            listeningCounts.forEach(lc -> excludeSongIds.add(lc.getSongId()));

            // Tập hợp genreId và artistId từ bài hát yêu thích và đã nghe
            Set<Integer> genreIds = new HashSet<>();
            Set<Integer> artistIds = new HashSet<>();
            for (Favorite favorite : favorites) {
                Song song = favorite.getSong();
                genreIds.add(song.getGenre().getId());
                artistIds.add(song.getArtist().getId());
            }
            for (ListeningCount lc : listeningCounts) {
                Song song = songRepository.findById(lc.getSongId())
                        .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));
                genreIds.add(song.getGenre().getId());
                artistIds.add(song.getArtist().getId());
            }

            // Tìm bài hát theo thể loại và ca sĩ
            List<Song> recommendedSongs = new ArrayList<>();
            for (Integer genreId : genreIds) {
                recommendedSongs.addAll(songRepository.findByGenreId(genreId));
            }
            for (Integer artistId : artistIds) {
                recommendedSongs.addAll(songRepository.findByArtistId(artistId));
            }

            // Loại bỏ bài hát đã nghe/yêu thích
            recommendedSongs = recommendedSongs.stream()
                    .filter(song -> !excludeSongIds.contains(song.getId()))
                    .distinct()
                    .limit(20) // Giới hạn 20 bài
                    .collect(Collectors.toList());

            // Nếu thiếu bài, bổ sung bài phổ biến
            if (recommendedSongs.size() < 20) {
                List<Object[]> topSongs = listeningCountRepository.findTopSongsByListenCount();
                for (Object[] result : topSongs) {
                    Integer songId = (Integer) result[0];
                    if (!excludeSongIds.contains(songId)) {
                        songRepository.findById(songId).ifPresent(recommendedSongs::add);
                    }
                    if (recommendedSongs.size() >= 20) break;
                }
            }

            // Ánh xạ sang SongResponse
            List<SongResponse> responses = recommendedSongs.stream()
                    .map(song -> new SongResponse(
                            song.getId(),
                            song.getTitle(),
                            song.getArtist() != null ? song.getArtist().getId() : null,
                            song.getArtist() != null ? song.getArtist().getName() : null,
                            song.getDuration(),
                            song.getFileUrl(),
                            song.getImageUrl(),
                            song.getGenre().getId(),
                            song.getGenre().getName()
                    ))
                    .collect(Collectors.toList());

            return new ResponseObject("success", "Lấy danh sách bài hát gợi ý thành công", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy danh sách gợi ý thất bại: " + e.getMessage(), null);
        }
    }
}