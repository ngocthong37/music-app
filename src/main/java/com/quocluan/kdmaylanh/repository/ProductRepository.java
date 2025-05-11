package com.quocluan.kdmaylanh.repository;

import com.quocluan.kdmaylanh.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p WHERE lower(p.name) LIKE lower(concat('%', :name, '%'))")
    List<Product> findByProductName(@Param("name") String name);

    @Query("SELECT p FROM Product p WHERE lower(p.brand.name) LIKE lower(concat('%', :brandName, '%'))")
    List<Product> findByBrandName(@Param("brandName") String brandName);

}
