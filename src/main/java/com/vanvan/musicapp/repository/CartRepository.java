package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

    @Query("SELECT c FROM Cart c WHERE c.product.id = :productId")
    Cart findCartByProductId(@Param("productId") Integer productId);

}
