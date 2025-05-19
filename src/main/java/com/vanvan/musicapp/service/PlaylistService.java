package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Playlist;
import com.vanvan.musicapp.entity.PlaylistSong;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.PlayListRepository;
import com.vanvan.musicapp.repository.PlaylistSongRepository;
import com.vanvan.musicapp.repository.SongRepository;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.request.PlaylistCreateRequest;
import com.vanvan.musicapp.response.PlaylistResponse;
import com.vanvan.musicapp.response.ResponseObject;
import com.vanvan.musicapp.response.SongResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlayListRepository playlistRepository;
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlaylistSongRepository playlistSongRepository;

    public ResponseObject createPlaylist(PlaylistCreateRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Playlist playlist = new Playlist();
            playlist.setTitle(request.getTitle());
            playlist.setUser(user);
            playlist.setCreatedAt(new Date());

            Playlist savedPlaylist = playlistRepository.save(playlist);

            if (request.getSongIds() != null && !request.getSongIds().isEmpty()) {
                addSongsToPlaylist(savedPlaylist.getId(), request.getSongIds());
            }

            return new ResponseObject("success", "Playlist created successfully", mapToResponse(savedPlaylist));
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to create playlist: " + e.getMessage(), null);
        }
    }


    public ResponseObject getPlaylist(Integer id) {
        try {
            Playlist playlist = playlistRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Playlist not found"));
            return new ResponseObject("success", "Playlist fetched successfully", mapToResponse(playlist));
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to fetch playlist: " + e.getMessage(), null);
        }
    }


    public ResponseObject getUserPlaylists(Integer userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Playlist> playlists = playlistRepository.findByUser(user);
            List<PlaylistResponse> responses = playlists.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
            return new ResponseObject("success", "Playlists fetched successfully", responses);
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to fetch playlists: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ResponseObject updatePlaylist(Integer id, PlaylistCreateRequest request) {
        try {
            Playlist playlist = playlistRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Playlist not found"));

            playlist.setTitle(request.getTitle());
            playlistRepository.save(playlist);

            playlistSongRepository.deleteByPlaylistId(id);
            if (request.getSongIds() != null && !request.getSongIds().isEmpty()) {
                addSongsToPlaylist(id, request.getSongIds());
            }

            return new ResponseObject("success", "Playlist updated successfully", mapToResponse(playlist));
        } catch (Exception e) {
            return new ResponseObject("error", "Failed to update playlist: " + e.getMessage(), null);
        }
    }


    private void addSongsToPlaylist(Integer playlistId, List<Integer> songIds) {
        for (int i = 0; i < songIds.size(); i++) {
            Integer songId = songIds.get(i);
            Song song = songRepository.findById(songId)
                    .orElseThrow(() -> new RuntimeException("Song not found: " + songId));

            PlaylistSong playlistSong = new PlaylistSong();
            playlistSong.setPlaylistId(playlistId);
            playlistSong.setSongId(songId);
            playlistSong.setOrderIndex(i);
            playlistSongRepository.save(playlistSong);
        }
    }

    private PlaylistResponse mapToResponse(Playlist playlist) {
        PlaylistResponse response = new PlaylistResponse();
        response.setId(playlist.getId());
        response.setTitle(playlist.getTitle());
        response.setUserId(playlist.getUser().getId());
        response.setCreatedAt(playlist.getCreatedAt());

        List<PlaylistSong> playlistSongs = playlistSongRepository.findByPlaylistId(playlist.getId());
        List<SongResponse> songResponses = new ArrayList<>();
        for (PlaylistSong playlistSong : playlistSongs) {
            Song song = songRepository.findById(playlistSong.getSongId())
                    .orElseThrow(() -> new RuntimeException("Song not found"));
            SongResponse songResponse = new SongResponse();
            songResponse.setId(song.getId());
            songResponse.setTitle(song.getTitle());
            songResponse.setFileUrl(song.getFileUrl());
            songResponse.setDuration(song.getDuration());
            songResponse.setArtistId(song.getArtist().getId());
            songResponse.setImageUrl(song.getImageUrl());
            songResponse.setArtistName(song.getArtist().getName());
//            songResponse.setOrderIndex(playlistSong.getOrderIndex());
            songResponses.add(songResponse);
        }
        response.setSongs(songResponses);

        return response;
    }
}