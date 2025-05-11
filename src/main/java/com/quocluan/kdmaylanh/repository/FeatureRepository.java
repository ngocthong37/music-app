package com.quocluan.kdmaylanh.repository;

import com.quocluan.kdmaylanh.entity.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface FeatureRepository extends JpaRepository<Feature, Integer> {
}
