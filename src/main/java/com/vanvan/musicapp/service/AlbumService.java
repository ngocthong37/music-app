package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Album;
import com.vanvan.musicapp.entity.AlbumSong;
import com.vanvan.musicapp.entity.Genre;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.repository.AlbumRepository;
import com.vanvan.musicapp.repository.AlbumSongRepository;
import com.vanvan.musicapp.repository.GenreRepository;
import com.vanvan.musicapp.repository.SongRepository;
import com.vanvan.musicapp.request.CreateAlbumRequest;
import com.vanvan.musicapp.response.AlbumDetailResponse;
import com.vanvan.musicapp.response.AlbumResponse;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.response.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final AlbumSongRepository albumSongRepository;
    private final GenreRepository genreRepository;
    private final StorageService storageService;

    public ResponseObject createAlbum(CreateAlbumRequest request) {
        try {
            Genre genre = null;
            if (request.getGenreId() != null) {
                Optional<Genre> optionalGenre = genreRepository.findById(request.getGenreId());
                if (optionalGenre.isPresent()) {
                    genre = optionalGenre.get();
                } else {
                    return new ResponseObject("error", "Genre not found", null);
                }
            }

            // Tạo album mới
            Album album = new Album();
            album.setTitle(request.getTitle());
            album.setCoverImageUrl(request.getCoverImageUrl());
            album.setCreatedAt(new Date());
            album.setGenre(genre);

            Album savedAlbum = albumRepository.save(album);

            if (request.getSongIds() != null && !request.getSongIds().isEmpty()) {
                for (Integer songId : request.getSongIds()) {
                    Optional<Song> song = songRepository.findById(songId);
                    if (song.isPresent()) {
                        AlbumSong albumSong = new AlbumSong();
                        albumSong.setAlbumId(savedAlbum.getId());
                        albumSong.setSongId(songId);
                        albumSongRepository.save(albumSong);
                    } else {
                        return new ResponseObject("error", "Song with ID " + songId + " not found", null);
                    }
                }
            }

            return new ResponseObject("success", "Album created successfully", savedAlbum.getId());
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to create album: " + e.getMessage(), null);
        }
    }

    public ResponseObject getAllAlbums() {
        try {
            List<Album> albums = albumRepository.findAll();

            List<AlbumResponse> albumResponses = albums.stream().map(album -> new AlbumResponse(
                    album.getId(),
                    album.getTitle(),
                    album.getCoverImageUrl(),
                    album.getCreatedAt(),
                    album.getGenre().getName()
            )).collect(Collectors.toList());


            return new ResponseObject("success", "Albums fetched successfully", albumResponses);
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to fetch albums: " + e.getMessage(), null);
        }
    }

    public ResponseObject getAlbumById(Integer albumId) {
        Optional<Album> optionalAlbum = albumRepository.findById(albumId);
        if (optionalAlbum.isEmpty()) {
            return new ResponseObject("error", "Album not found", null);
        }

        Album album = optionalAlbum.get();
        String genreName = album.getGenre() != null ? album.getGenre().getName() : null;

        List<AlbumSong> albumSongs = albumSongRepository.findByAlbumId(albumId);

        List<SongResponse> songResponses = albumSongs.stream()
                .map(albumSong -> {
                    Song song = songRepository.findById(albumSong.getSongId()).orElse(null);
                    if (song == null) return null;
                    return new SongResponse(
                            song.getId(),
                            song.getTitle(),
                            song.getArtist() != null ? song.getArtist().getName() : null,
                            song.getDuration(),
                            song.getFileUrl()
                    );
                })
                .filter(Objects::nonNull)
                .toList();

        AlbumDetailResponse response = new AlbumDetailResponse(
                album.getId(),
                album.getTitle(),
                album.getCoverImageUrl(),
                album.getCreatedAt(),
                genreName,
                songResponses
        );

        return new ResponseObject("success", "Album fetched successfully", response);
    }

    public String uploadImage(MultipartFile file, String namePath, Integer albumId) {
        String imageUrl = storageService.uploadImages(file, namePath);
        albumRepository.updateImage(imageUrl, albumId);
        return imageUrl;
    }


}
