package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Genre;
import org.springframework.data.repository.CrudRepository;

public interface GenreRepository extends CrudRepository<Genre, Integer> {
}
