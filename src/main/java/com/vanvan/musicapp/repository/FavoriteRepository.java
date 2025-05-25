package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Favorite;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndSong(User user, Song song);
    boolean existsByUserAndSong(User user, Song song);
    void deleteByUserAndSong(User user, Song song);
    List<Favorite> findByUserId(Integer userId);

    @Query("SELECT f.song.id, COUNT(f) as favoriteCount " +
            "FROM Favorite f " +
            "WHERE f.createdAt >= :thirtyDaysAgo " +
            "GROUP BY f.song.id " +
            "ORDER BY favoriteCount DESC")
    List<Object[]> findTopFavoriteSongs(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    @Query("SELECT s.genre.id, s.genre.name, COUNT(f) as favoriteCount " +
            "FROM Favorite f " +
            "JOIN Song s ON f.song.id = s.id " +
            "WHERE f.createdAt >= :thirtyDaysAgo " +
            "GROUP BY s.genre.id, s.genre.name " +
            "ORDER BY favoriteCount DESC")
    List<Object[]> findTopFavoriteGenres(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);

    List<Favorite> findByUserIdIn(Set<Integer> userIds);
}