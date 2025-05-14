package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.PlaylistSong;
import com.vanvan.musicapp.entity.PlaylistSongId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, PlaylistSongId> {
    List<PlaylistSong> findByPlaylistId(Integer playlistId);
    void deleteByPlaylistId(Integer playlistId);
}