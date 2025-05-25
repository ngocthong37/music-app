package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.ListeningCount;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.repository.ListeningCountRepository;
import com.vanvan.musicapp.repository.SongRepository;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.response.ArtistResponse;
import com.vanvan.musicapp.response.ListeningCountResponse;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.response.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListeningCountService {
    private final ListeningCountRepository listeningCountRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;

    public ResponseObject incrementListeningCount(Integer userId, Integer songId) {
        try {
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));

            ListeningCount listeningCount = listeningCountRepository.findByUserIdAndSongId(userId, songId)
                    .orElse(new ListeningCount());

            listeningCount.setUserId(userId);
            listeningCount.setSongId(songId);
            listeningCount.setCount(listeningCount.getCount() + 1);
            listeningCount.setListenTime(new Date());

            ListeningCount savedCount = listeningCountRepository.save(listeningCount);

            SongResponse songResponse = new SongResponse(
                    song.getId(),
                    song.getTitle(),
                    song.getArtist() != null ? song.getArtist().getId() : null,
                    song.getArtist() != null ? song.getArtist().getName() : null,
                    song.getDuration(),
                    song.getFileUrl(),
                    song.getImageUrl(),
                    song.getGenre().getId(),
                    song.getGenre().getName(),
                    null
            );

            ListeningCountResponse response = new ListeningCountResponse();
            response.setId(savedCount.getId());
            response.setSong(songResponse);
            response.setCount(savedCount.getCount());
            response.setListenTime(savedCount.getListenTime());

            return new ResponseObject("success", "Tăng lượt nghe thành công", response.getId());
        } catch (Exception e) {
            return new ResponseObject("error", "Tăng lượt nghe thất bại: " + e.getMessage(), null);
        }
    }

    public ResponseObject getListeningCountsByUserId(Integer userId) {
        try {
            userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
            List<ListeningCount> listeningCounts = listeningCountRepository.findByUserId(userId);
            List<ListeningCountResponse> responses = listeningCounts.stream()
                    .map(count -> {
                        Song song = songRepository.findById(count.getSongId())
                                .orElseThrow(() -> new RuntimeException("Bài hát không tồn tại"));
                        SongResponse songResponse = new SongResponse(
                                song.getId(),
                                song.getTitle(),
                                song.getArtist() != null ? song.getArtist().getId() : null,
                                song.getArtist() != null ? song.getArtist().getName() : null,
                                song.getDuration(),
                                song.getFileUrl(),
                                song.getImageUrl(),
                                song.getGenre().getId(),
                                song.getGenre().getName(),
                                null
                        );
                        ListeningCountResponse response = new ListeningCountResponse();
                        response.setId(count.getId());
                        response.setSong(songResponse);
                        response.setCount(count.getCount());
                        response.setListenTime(count.getListenTime());
                        return response;
                    })
                    .collect(Collectors.toList());
            return new ResponseObject("success", "Lấy danh sách lượt nghe thành công", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy danh sách lượt nghe thất bại: " + e.getMessage(), null);
        }
    }

    public ResponseObject getTop10SongsByListenCount() {
        try {
            List<Object[]> topSongs = listeningCountRepository.findTopSongsByListenCount();

            List<SongResponse> responses = topSongs.stream()
                    .map(result -> {
                        Integer songId = (Integer) result[0];
                        Long listenCount = (Long) result[1];

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
                                listenCount,
                                null,
                                null
                        );
                    })
                    .sorted((a, b) -> Long.compare(b.getListenCount(), a.getListenCount())) // sắp xếp giảm dần
                    .limit(10)
                    .collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();

            return new ResponseObject("success", "Lấy top 10 bài hát được nghe nhiều nhất thành công", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy top 10 bài hát thất bại: " + e.getMessage(), null);
        }
    }


    public ResponseObject getTop10ArtistsByListenCount() {
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            List<Object[]> topArtists = listeningCountRepository.findTopArtistsByListenCount(thirtyDaysAgo);
            List<ArtistResponse> responses = topArtists.stream()
                    .limit(10)
                    .map(result -> {
                        Integer artistId = ((Number) result[0]).intValue();
                        String artistName = result[1] != null ? result[1].toString() : null;
                        Long listenCount = ((Number) result[2]).longValue();
                        return new ArtistResponse(artistId, artistName, null, listenCount, null, null);
                    })
                    .collect(Collectors.toList());
            return new ResponseObject("success", "Lấy top 10 nghệ sĩ được nghe nhiều nhất thành công", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy top 10 nghệ sĩ thất bại: " + e.getMessage(), null);
        }
    }
}