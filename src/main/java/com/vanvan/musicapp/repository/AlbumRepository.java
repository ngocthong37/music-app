package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Album;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE Album a SET a.coverImageUrl = :image where a.id = :albumId")
    void updateImage(String image, Integer albumId);
}
