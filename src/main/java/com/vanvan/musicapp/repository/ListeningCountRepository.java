package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.ListeningCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ListeningCountRepository extends JpaRepository<ListeningCount, Integer> {
    List<ListeningCount> findByUserId(Integer userId);
    Optional<ListeningCount> findByUserIdAndSongId(Integer userId, Integer songId);

    @Query("SELECT lc.songId, SUM(lc.count) as totalCount " +
            "FROM ListeningCount lc " +
            "GROUP BY lc.songId " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopSongsByListenCount();

    @Query("SELECT s.artist.id, s.artist.name, SUM(lc.count) as totalCount " +
            "FROM ListeningCount lc " +
            "JOIN Song s ON lc.songId = s.id " +
            "WHERE s.artist IS NOT NULL " +
            "GROUP BY s.artist.id, s.artist.name " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopArtistsByListenCount();
}