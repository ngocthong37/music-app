package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Artist;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE Artist a SET a.imageUrl = :image where a.id = :artistId")
    void updateImage(String image, Integer artistId);
}
