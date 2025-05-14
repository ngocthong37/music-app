package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Artist;
import com.vanvan.musicapp.entity.Genre;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.repository.ArtistRepository;
import com.vanvan.musicapp.repository.GenreRepository;
import com.vanvan.musicapp.repository.SongRepository;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final StorageService storageService;

    public ResponseObject createSong(String title, Integer artistId, Integer genreId, int duration, String lyrics, MultipartFile file) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artist not found"));
        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new RuntimeException("Genre not found"));

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

            Song song = new Song();
            song.setTitle(title);
            song.setArtist(artist);
            song.setGenre(genre);
            song.setDuration(duration);
            song.setFileUrl("/api/v1/musics/" + fileName);
            song.setLyrics(lyrics);
            song.setCreatedAt(new Date());

            Song savedSong = songRepository.save(song);

            return new ResponseObject("success", "song created successfully", savedSong.getId());

        } catch (Exception e) {
            return new ResponseObject("error", "Failed to create album: " + e.getMessage(), null);
        }
    }



    public Song updateSong(Integer id, SongRequest req) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        song.setTitle(req.getTitle());
        song.setArtist(artistRepository.findById(req.getArtistId()).orElseThrow());
        song.setGenre(genreRepository.findById(req.getGenreId()).orElseThrow());
        song.setDuration(req.getDuration());
        song.setFileUrl(req.getFileUrl());
        song.setLyrics(req.getLyrics());

        return songRepository.save(song);
    }

    public void deleteSong(Integer id) {
        songRepository.deleteById(id);
    }

    public ResponseObject getAllSongs() {
        try {
            List<Song> songs = songRepository.findAll();
            List<SongResponse> songResponses = songs.stream().map(song -> new SongResponse(
                    song.getId(),
                    song.getTitle(),
                    song.getArtist() != null ? song.getArtist().getId() : null,
                    song.getDuration(),
                    song.getFileUrl(),
                    song.getImageUrl(),
                    song.getGenre().getId()

            )).collect(Collectors.toList());

            return new ResponseObject("success", "Songs fetched successfully", songResponses);
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
                    song.getDuration(),
                    song.getFileUrl(),
                    song.getImageUrl(),
                    song.getGenre().getId()
            )).collect(Collectors.toList());

            return new ResponseObject("success", "Songs found", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Search failed: " + e.getMessage(), null);
        }
    }


}
