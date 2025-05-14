package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Playlist;
import com.vanvan.musicapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayListRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findByUser(User user);
}
