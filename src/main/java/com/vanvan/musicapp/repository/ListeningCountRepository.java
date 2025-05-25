package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.ListeningCount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
            "AND lc.listenTime >= :thirtyDaysAgo " +
            "GROUP BY s.artist.id, s.artist.name " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopArtistsByListenCount(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    @Query("SELECT lc.songId, SUM(lc.count) as totalCount " +
            "FROM ListeningCount lc " +
            "GROUP BY lc.songId " +
            "ORDER BY totalCount DESC")
    List<Object[]> findTopSongsByListenCount(Pageable pageable);

    List<ListeningCount> findByUserIdIn(Set<Integer> userIds);
}