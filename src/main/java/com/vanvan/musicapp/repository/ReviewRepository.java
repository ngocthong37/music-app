package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.Reviews;
import com.vanvan.musicapp.entity.ReviewsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews, ReviewsId> {
    @Query(value = "SELECT od.orders.orderID, od.product.productID, od.isReview " +
            "FROM OrderDetail od " +
            "WHERE od.product.productID = :productId " +
            "ORDER BY od.orders.orderDate DESC")
    List<Object[]> findAllOrderedProduct(@Param("productId") Integer productId);

    @Query(value = "SELECT r FROM Reviews r " +
            "WHERE r.product.productID = :productId")
    Reviews findReviewByProductID(@Param("productId") Integer productId);

}
