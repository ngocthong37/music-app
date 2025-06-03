package com.vanvan.musicapp.service;

import com.mpatric.mp3agic.Mp3File;
import com.vanvan.musicapp.entity.Artist;
import com.vanvan.musicapp.entity.Genre;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.*;
import com.vanvan.musicapp.request.SongRequest;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.response.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final StorageService storageService;
    private final UserRepository userRepository;
    private final ListeningCountRepository listeningCountRepository;

    public ResponseObject createSong(String title, Integer artistId, Integer genreId, Integer userId, String lyrics, MultipartFile file) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                throw new RuntimeException("File name is invalid");
            }

            String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String fileName = System.currentTimeMillis() + "_" + title.replaceAll("[^a-zA-Z0-9]", "_") + extension;

            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);

            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 👇 Lấy duration từ file MP3
            Mp3File mp3File = new Mp3File(filePath.toFile());
            int durationInSeconds = (int) mp3File.getLengthInSeconds();

            Song song = new Song();
            song.setTitle(title);
            song.setArtist(artist);
            song.setGenre(genre);
            song.setUser(user); // 👈 Gán user vào bài hát
            song.setDuration(durationInSeconds);
            song.setFileUrl("/api/v1/musics/" + fileName);
            song.setLyrics(lyrics);
            song.setCreatedAt(new Date());

            Song savedSong = songRepository.save(song);

            return new ResponseObject("success", "Song created successfully", savedSong.getId());

        } catch (Exception e) {
            return new ResponseObject("error", "Failed to create song: " + e.getMessage(), null);
        }
    }



    public ResponseObject updateSong(Integer id, String title, Integer artistId, Integer genreId, String lyrics, MultipartFile file) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài hát"));

        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ"));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thể loại"));

        try {
            song.setTitle(title);
            song.setArtist(artist);
            song.setGenre(genre);
            song.setLyrics(lyrics);
            song.setUpdatedAt(new Date());

            if (file != null && !file.isEmpty()) {
                String originalFileName = file.getOriginalFilename();
                if (originalFileName == null || originalFileName.isBlank()) {
                    throw new RuntimeException("Tên tệp không hợp lệ");
                }

                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = System.currentTimeMillis() + "_" + title.replaceAll("[^a-zA-Z0-9]", "_") + extension;

                Path uploadPath = Paths.get("uploads");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Xóa file cũ nếu có
                if (song.getFileUrl() != null && !song.getFileUrl().isBlank()) {
                    String oldFileName = song.getFileUrl().substring(song.getFileUrl().lastIndexOf("/") + 1);
                    Path oldFilePath = uploadPath.resolve(oldFileName);
                    Files.deleteIfExists(oldFilePath);
                }

                // Lưu file mới
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 👉 Tính duration từ file mp3
                Mp3File mp3File = new Mp3File(filePath.toFile());
                int durationInSeconds = (int) mp3File.getLengthInSeconds();

                song.setDuration(durationInSeconds);
                song.setFileUrl("/api/v1/musics/" + fileName);
            }

            Song updatedSong = songRepository.save(song);
            return new ResponseObject("success", "Cập nhật bài hát thành công", updatedSong.getId());

        } catch (Exception e) {
            return new ResponseObject("error", "Lỗi khi cập nhật bài hát: " + e.getMessage(), null);
        }
    }

    public void deleteSong(Integer id) {
        songRepository.deleteById(id);
    }

    public ResponseObject getAllSongsAdmin() {
        try {
            List<Song> songs = songRepository.findAll();
            List<SongResponse> songResponses = songs.stream().map(song -> new SongResponse(
                    song.getId(),
                    song.getTitle(),
                    song.getArtist() != null ? song.getArtist().getId() : null,
                    song.getArtist() != null ? song.getArtist().getName() : null,
                    song.getDuration(),
                    song.getFileUrl(),
                    song.getImageUrl(),
                    song.getGenre().getId(),
                    song.getGenre().getName(),
                    song.getCreatedAt()
            )).collect(Collectors.toList());
            Collections.reverse(songResponses);
            Map<String, Object> data = new HashMap<>();
            return new ResponseObject("success", "Songs fetched successfully", songResponses);
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to fetch songs: " + e.getMessage(), null);
        }
    }


    public ResponseObject getAllSongs() {
        try {
            List<Song> songs = songRepository.findAll();
            List<Object[]> listenCounts = listeningCountRepository.findTopSongsByListenCount();

            // Create a map of songId to listenCount for efficient lookup
            Map<Integer, Long> listenCountMap = listenCounts.stream()
                    .collect(Collectors.toMap(
                            result -> (Integer) result[0],
                            result -> (Long) result[1],
                            (existing, replacement) -> existing // Handle duplicates, keep existing
                    ));

            List<SongResponse> songResponses = songs.stream().map(song -> new SongResponse(
                    song.getId(),
                    song.getTitle(),
                    song.getArtist() != null ? song.getArtist().getId() : null,
                    song.getArtist() != null ? song.getArtist().getName() : null,
                    song.getDuration(),
                    song.getFileUrl(),
                    song.getImageUrl(),
                    song.getGenre().getId(),
                    song.getGenre().getName(),
                    listenCountMap.getOrDefault(song.getId(), 0L),
                    null,
                    null
            )).collect(Collectors.toList());

            Collections.reverse(songResponses);

            Map<String, Object> data = new HashMap<>();
            data.put("songs", songResponses);
            return new ResponseObject("success", "Songs fetched successfully", data);
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to fetch songs: " + e.getMessage(), null);
        }
    }



    public String uploadImage(MultipartFile file, String namePath, Integer songId) {
        String imageUrl = storageService.uploadImages(file, namePath);
        songRepository.updateImage(imageUrl, songId);
        return imageUrl;
    }

    public ResponseObject searchSongs(String keyword) {
        try {
            List<Song> songs = songRepository.searchByTitleOrArtistName(keyword);
            List<SongResponse> responses = songs.stream().map(song -> new SongResponse(
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
            )).collect(Collectors.toList());

            return new ResponseObject("success", "Songs found", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Search failed: " + e.getMessage(), null);
        }
    }

    public ResponseObject getSongsByArtistId(Integer artistId) {
        try {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nghệ sĩ với ID: " + artistId));

            List<Song> songs = songRepository.findByArtistId(artistId);
            if (songs.isEmpty()) {
                return new ResponseObject("success", "Không có bài hát nào cho nghệ sĩ này", new HashMap<>());
            }

            List<SongResponse> songResponses = songs.stream().map(song -> new SongResponse(
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
            )).collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();
            data.put("songs", songResponses);
            return new ResponseObject("success", "Songs fetched successfully", data);
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to fetch songs: " + e.getMessage(), null);
        }
    }


}
