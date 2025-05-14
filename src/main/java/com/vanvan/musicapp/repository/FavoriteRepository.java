package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Favorite;
import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
    List<Favorite> findByUser(User user);
    Optional<Favorite> findByUserAndSong(User user, Song song);
    boolean existsByUserAndSong(User user, Song song);
    void deleteByUserAndSong(User user, Song song);
    List<Favorite> findByUserId(Integer userId);
}