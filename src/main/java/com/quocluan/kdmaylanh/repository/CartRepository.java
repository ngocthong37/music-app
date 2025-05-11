package com.quocluan.kdmaylanh.repository;

import com.quocluan.kdmaylanh.entity.Cart;
import com.quocluan.kdmaylanh.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.product.id = :productId")
    Cart findCartByProductId(@Param("productId") Integer productId);

}
