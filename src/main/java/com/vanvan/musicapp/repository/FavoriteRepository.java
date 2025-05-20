package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Favorite;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndSong(User user, Song song);
    boolean existsByUserAndSong(User user, Song song);
    void deleteByUserAndSong(User user, Song song);
    List<Favorite> findByUserId(Integer userId);

    @Query("SELECT f.song.id, COUNT(f) as favoriteCount " +
            "FROM Favorite f " +
            "GROUP BY f.song.id " +
            "ORDER BY favoriteCount DESC")
    List<Object[]> findTopFavoriteSongs();

    @Query("SELECT s.genre.id, s.genre.name, COUNT(f) as favoriteCount " +
            "FROM Favorite f " +
            "JOIN Song s ON f.song.id = s.id " +
            "GROUP BY s.genre.id, s.genre.name " +
            "ORDER BY favoriteCount DESC")
    List<Object[]> findTopFavoriteGenres();
}