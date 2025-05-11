package com.vanvan.musicapp.repository;

import com.vanvan.musicapp.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    @Query(value = "SELECT od FROM OrderDetail od " +
            "WHERE od.orders.orderID = :orderId " +
            "ORDER BY od.orders.orderDate DESC " +  // Sắp xếp theo thời gian order giảm dần
            "LIMIT 1")                               // Chỉ lấy 1 bản ghi
    Optional<OrderDetail> findLatestOrderByOrderId(Integer orderId);

}
