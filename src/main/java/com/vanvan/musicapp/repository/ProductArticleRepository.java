package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.ProductArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductArticleRepository extends JpaRepository<ProductArticle, Integer> {



}
