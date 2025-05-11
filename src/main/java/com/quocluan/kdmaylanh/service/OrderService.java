package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.entity.*;
import com.quocluan.kdmaylanh.model.OrderDetailModel;
import com.quocluan.kdmaylanh.model.OrderItemModel;
import com.quocluan.kdmaylanh.model.OrderModel;
import com.quocluan.kdmaylanh.model.OrderedConfirmedModel;
import com.quocluan.kdmaylanh.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    public ResponseEntity<ResponseObject> addOrder(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(json);

            // Lưu thông tin đơn hàng
            Integer customerId = jsonNode.get("customerId").asInt();
            Integer addressId = jsonNode.get("addressId").asInt();
            Date orderDate = new Date();
            Date updateDate = new Date();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));

            Orders order = new Orders();
            order.setCustomer(customer);
            order.setAddress(address);
            order.setOrderDate(orderDate);

            Orders savedOrder = orderRepository.save(order);
            JsonNode orderItemsNode = jsonNode.get("orderItem");
            for (JsonNode orderItemNode : orderItemsNode) {
                Integer productId = orderItemNode.get("productId").asInt();
                Integer quantity = orderItemNode.get("quantity").asInt();
                Double unitPrice = orderItemNode.get("unitPrice").doubleValue();

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrders(savedOrder);
                orderDetail.setProduct(product);
                orderDetail.setQuantity(quantity);
                orderDetail.setUnitPrice(unitPrice);
                orderDetail.setIsReview(0);

                orderDetailRepository.save(orderDetail);
            }

            // Lưu thông tin trạng thái đơn hàng
            OrderStatus orderStatus = new OrderStatus();
            OrderStatusId orderStatusId = new OrderStatusId();
            orderStatusId.setDateUpdate(updateDate);
            orderStatusId.setOrders(savedOrder);
            orderStatus.setId(orderStatusId);


            Status status = new Status();
            status.setStatusID("PENDING");
            orderStatus.setStatus(status);
            orderStatus.setEmployeeID(null);
            orderStatus.setNote("Waiting for confirmation");

            orderStatusRepository.save(orderStatus);

            return ResponseEntity.ok().body(new ResponseObject("OK", "Order added successfully", savedOrder.getOrderID()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }


    public ResponseEntity<ResponseObject> findAllOrder() {
        List<Object[]> resultList = orderStatusRepository.findAllOrdersInfo();
        List<OrderedConfirmedModel> orderedModels = new ArrayList<>();
        for (Object[] result : resultList) {
            Integer orderId = (Integer) result[0];
            Date orderDate = (Date) result[1];
            Integer employeeID = null;
            employeeID = (Integer) result[2];

            String statusName = (String) result[3];
            Integer customerId = (Integer) result[4];
            String employeeName = null;
            employeeName = (String) result[5];
            OrderedConfirmedModel orderedModel = new OrderedConfirmedModel(orderId, orderDate, employeeID, statusName, customerId, employeeName);
            orderedModels.add(orderedModel);
        }

        if (orderedModels.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", orderedModels));
    }

    public ResponseEntity<ResponseObject> findOrderByOrderId(Integer orderId) {
        List<Object[]> resultList = orderStatusRepository.findOrderByOrderId(orderId);
        Map<Integer, OrderDetailModel> orderMap = new HashMap<>();

        for (Object[] data : resultList) {
            orderId = (Integer) data[0];
            Integer quantity = (Integer) data[2];
            Double price = (Double) data[8];
            Date orderDate = (Date) data[1];
            OrderDetailModel orderDetail = orderMap.getOrDefault(orderId, new OrderDetailModel());
            orderDetail.setOrderId(orderId);
            orderDetail.setStatus((String) data[3]);
            orderDetail.setOrderDate(orderDate);
            orderDetail.setTotalQuantity(orderDetail.getTotalQuantity() == null ? quantity : orderDetail.getTotalQuantity() + quantity);
            orderDetail.setTotalMoney(orderDetail.getTotalMoney() == null ? quantity * price : orderDetail.getTotalMoney() + (quantity * price));

            OrderItemModel orderItem = new OrderItemModel();
            orderItem.setProductId((Integer) data[5]);
            orderItem.setProductName((String) data[7]);
            orderItem.setProductUrl((String) data[6]);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(price);

            if (orderDetail.getOrderItems() == null) {
                orderDetail.setOrderItems(new ArrayList<>());
            }
            orderDetail.getOrderItems().add(orderItem);

            orderMap.put(orderId, orderDetail);
        }
        if (resultList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", new ArrayList<>(orderMap.values())));
    }

    public ResponseEntity<ResponseObject> findAllOrderByCustomerID(Integer customerID) {
        List<Object[]> resultList = orderStatusRepository.findAllOrderByCustomerID(customerID);
        List<OrderModel> orderList = convertToObjectList(resultList);

        if (orderList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", orderList));
    }

    public ResponseEntity<ResponseObject> findAllOrderByStatusID(String statusID) {
        List<Object[]> resultList = orderStatusRepository.findAllOrderByStatusID(statusID);
        List<OrderModel> orderList = convertToObjectList(resultList);

        if (orderList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("Not found", "Not found", ""));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "Successfully", orderList));
    }

    public ResponseEntity<Object> cancelOrder(Integer orderId) {
        try {
            Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findOrderStatusByOrderId(orderId);
            if (orderStatusOptional.isPresent()) {
                OrderStatus orderStatus = orderStatusOptional.get();
                Status status1 = new Status();
                List<String> validStatus = Arrays.asList("CANCELED", "COMPLETED", "REJECTED", "DELIVERED", "PAID");
                if (validStatus.contains(orderStatus.getStatus().getStatusID())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "Cannot update this order", ""));
                }
                status1.setStatusID("CANCELED");
                orderStatus.setStatus(status1);
                orderStatusRepository.save(orderStatus);
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseObject("OK", "canceled order successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "order status not found with ID: " + orderId, null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "An error occurred", e.getMessage()));
        }
    }

    public List<OrderModel> convertToObjectList(List<Object[]> resultList) {
        List<OrderModel> orderList = new ArrayList<>();

        for (Object[] data : resultList) {
            Integer orderId = (Integer) data[0];
            Date orderDate = (Date) data[1];
            String status = (String) data[2];
            Double price = (Double) data[3];
            Integer quantity = (Integer) data[4];
            String customerName = (String) data[5];
            Integer customerId = (Integer) data[6];

            OrderModel existingOrder = orderList.stream().filter(order -> order.getOrderId().equals(orderId)).findFirst().orElse(null);

            if (existingOrder == null) {
                OrderModel newOrder = new OrderModel();
                newOrder.setOrderId(orderId);
                newOrder.setOrderDate(orderDate);
                newOrder.setStatus(status);
                newOrder.setTotalPrice(price * quantity);
                newOrder.setTotalQuantity(quantity);
                newOrder.setCustomerName(customerName);
                newOrder.setCustomerId(customerId);
                orderList.add(newOrder);
            } else {
                existingOrder.setTotalPrice(existingOrder.getTotalPrice() + (price * quantity));
                existingOrder.setTotalQuantity(existingOrder.getTotalQuantity() + quantity);
            }
        }

        return orderList;
    }



}
