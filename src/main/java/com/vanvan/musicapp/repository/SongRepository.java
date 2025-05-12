package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer> {
}
