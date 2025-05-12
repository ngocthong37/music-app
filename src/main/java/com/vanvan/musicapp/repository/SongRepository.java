package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Song;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SongRepository extends JpaRepository<Song, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE Song s SET s.imageUrl = :image where s.id = :songId")
    void updateImage(String image, Integer songId);
}
