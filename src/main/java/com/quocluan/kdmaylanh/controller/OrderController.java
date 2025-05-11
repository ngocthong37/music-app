package com.quocluan.kdmaylanh.controller;


import com.quocluan.kdmaylanh.entity.ResponseObject;
import com.quocluan.kdmaylanh.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping(path = "/api/v1/")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("order/addOrder")
    public ResponseEntity<ResponseObject> addOrder(@RequestBody String json) {
        return orderService.addOrder(json);
    }

    // Đơn hàng đã được xác nhận
    @GetMapping("management/order/findAllOrderedConfirmedByEmployee")
    public ResponseEntity<ResponseObject> findAll() {
        return orderService.findAllOrder();
    }

    @GetMapping("order/findOrderByOrderId/{orderId}")
    public ResponseEntity<ResponseObject> findOrderByOrderId(@PathVariable Integer orderId) {
        return orderService.findOrderByOrderId(orderId);
    }

    @GetMapping("order/findAllOrderByCustomerId/{customerId}")
    public ResponseEntity<ResponseObject> findAllOrderByCustomerId(@PathVariable Integer customerId) {
        return orderService.findAllOrderByCustomerID(customerId);
    }

    @GetMapping("employee/order/findAllOrderByStatusId/{statusId}")
    public ResponseEntity<ResponseObject> findAllOrderByStatusId(@PathVariable String statusId) {
        return orderService.findAllOrderByStatusID(statusId);
    }

    @PutMapping("order/cancelOrder/{orderId}")
    ResponseEntity<Object> cancelOrder(@PathVariable Integer orderId) {
        return orderService.cancelOrder(orderId);
    }


}
