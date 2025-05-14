package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
