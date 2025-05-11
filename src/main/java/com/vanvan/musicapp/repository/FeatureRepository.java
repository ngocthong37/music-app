package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface FeatureRepository extends JpaRepository<Feature, Integer> {
}
