package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
}
