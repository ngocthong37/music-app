package com.quocluan.kdmaylanh.repository;

import com.quocluan.kdmaylanh.entity.ProductFeature;
import com.quocluan.kdmaylanh.entity.ProductFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductFeatureRepository extends JpaRepository<ProductFeature, ProductFeatureId> {

    @Query("SELECT pf FROM ProductFeature pf WHERE pf.product.productID = :productId")
    Optional<ProductFeature> findFeatureByProductId(@Param("productId") Integer productId);


}


