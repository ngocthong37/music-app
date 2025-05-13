package com.vanvan.musicapp.repository;


import com.vanvan.musicapp.entity.AlbumSong;
import com.vanvan.musicapp.entity.AlbumSongId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumSongRepository extends JpaRepository<AlbumSong, AlbumSongId> {
    List<AlbumSong> findByAlbumId(Integer albumId);
}
