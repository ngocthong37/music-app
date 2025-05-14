package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Song;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer> {
    @Transactional
    @Modifying
    @Query("UPDATE Song s SET s.imageUrl = :image where s.id = :songId")
    void updateImage(String image, Integer songId);

    @Query("SELECT s FROM Song s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.artist.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Song> searchByTitleOrArtistName(@Param("keyword") String keyword);

    List<Song> findByGenreId(Integer genreId);
    List<Song> findByArtistId(Integer artistId);
    List<Song> findByIdNotIn(List<Integer> songIds);

}
