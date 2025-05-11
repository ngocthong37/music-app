package com.quocluan.kdmaylanh.repository;
import com.quocluan.kdmaylanh.entity.OrderStatus;
import com.quocluan.kdmaylanh.entity.OrderStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, OrderStatusId> {
    @Query("SELECT os FROM OrderStatus os WHERE os.id.orders.orderID = :orderId")
    Optional<OrderStatus> findOrderStatusByOrderId(@Param("orderId") Integer orderId);

    @Query(value = "SELECT o.orderID, o.orderDate, os.employeeID, s.statusName, o.customer.customerID, e.name " +
            "FROM OrderStatus os " +
            "INNER JOIN Orders o ON os.id.orders.orderID = o.orderID " +
            "INNER JOIN Status s ON os.status.statusID = s.statusID " +
            "INNER JOIN Employee e ON os.employeeID = e.employeeID")
    List<Object[]> findAllOrdersInfo();

    @Query(value = "SELECT o.orderID, o.orderDate, od.quantity, s.statusName, o.customer.customerID, od.product.productID, pi.imagePath, od.product.name, od.product.price " +
            "FROM OrderStatus os " +
            "INNER JOIN Orders o ON os.id.orders.orderID = :orderId " +
            "INNER JOIN OrderDetail od ON od.orders.orderID = :orderId " +
            "INNER JOIN Status s ON os.status.statusID = s.statusID " +
            "INNER JOIN ProductImage pi ON od.product.productID = pi.product.productID " +
            "WHERE o.orderID = :orderId")
    List<Object[]> findOrderByOrderId(@Param("orderId") Integer orderId);

    @Query(value = "SELECT o.orderID, o.orderDate, os.status.statusName, od.unitPrice, od.quantity, o.customer.name, o.customer.customerID " +
            "FROM Orders o " +
            "INNER JOIN Customer c ON o.customer.customerID = :customerId " +
            "INNER JOIN OrderDetail od ON od.orders.orderID = o.orderID " +
            "INNER JOIN OrderStatus os ON os.id.orders.orderID = o.orderID " +
            "WHERE c.customerID = :customerId")
    List<Object[]> findAllOrderByCustomerID(@Param("customerId") Integer customerId);

    @Query(value = "SELECT o.orderID, o.orderDate, os.status.statusName, od.unitPrice, od.quantity, o.customer.name, o.customer.customerID " +
            "FROM Orders o " +
            "INNER JOIN Customer c ON o.customer.customerID = o.customer.customerID " +
            "INNER JOIN OrderDetail od ON od.orders.orderID = o.orderID " +
            "INNER JOIN OrderStatus os ON os.id.orders.orderID = o.orderID " +
            "WHERE os.status.statusID = :statusID")
    List<Object[]> findAllOrderByStatusID(@Param("statusID") String statusID);



}
