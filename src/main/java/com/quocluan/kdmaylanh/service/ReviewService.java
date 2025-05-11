package com.quocluan.kdmaylanh.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quocluan.kdmaylanh.entity.*;
import com.quocluan.kdmaylanh.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;

    public String checkReview(Integer productId) {
        List<Object[]> resultList = reviewRepository.findAllOrderedProduct(productId);
        Object[] checkData = null;
        checkData = resultList.get(0);
        if ((Integer) checkData[2] == 1) {
            return "NOT ALLOW";
        }
        for (Object[] data : resultList) {
            Integer isReview = (Integer) data[2];
            if (isReview == 1) {
                return "UPDATE";
            }
        }
        return "ADD";

    }

    public ResponseEntity<Object> addReview(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            Integer orderId = jsonNode.get("orderId").asInt();
            Integer starsRating = jsonNode.get("starsRating").asInt();
            Date reviewDate = new Date();
            Integer productId = jsonNode.get("productId").asInt();
            String statusReview = checkReview(productId);
            String reviewContent = jsonNode.get("reviewContent").asText();
            Reviews review;
            if (Objects.equals(statusReview, "UPDATE")) {
                Optional<Reviews> optionalReview = Optional.ofNullable(reviewRepository.findReviewByProductID(productId));
                if (optionalReview.isPresent()) {
                    reviewRepository.delete(optionalReview.get());  // Delete the existing review
                    review = new Reviews();  // Create a new review instance
                    ReviewsId reviewsId = new ReviewsId(productId, orderId);
                    review.setId(reviewsId);
                    review.setReviewDate(reviewDate);
                    review.setReviewContent(reviewContent);
                    review.setStarsRating(starsRating);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "Review not found for product " + productId, null));
                }
            } else if (Objects.equals(statusReview, "NOT ALLOW")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "Can't review this product " + productId, null));
            } else {
                review = new Reviews();
            }
            Optional<OrderStatus> orderStatusOptional = orderStatusRepository.findOrderStatusByOrderId(orderId);
            if (orderStatusOptional.isPresent()) {
                OrderStatus orderStatus = orderStatusOptional.get();
                List<String> validStatus = Arrays.asList("CANCELED", "PAID", "REJECTED", "DELIVERED", "PROCESSING");
                if (validStatus.contains(orderStatus.getStatus().getStatusID())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("ERROR", "Cannot add review", ""));
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "order status not found with ID: " + orderId, null));
            }
            ReviewsId reviewsId = new ReviewsId(productId, orderId);
            review.setId(reviewsId);
            Optional<Orders> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isPresent()) {
                review.setOrders(orderOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "order not found with ID: " + orderId, null));
            }
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                review.setProduct(productOptional.get());
            }
            else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "product not found with ID: " + orderId, null));
            }
            review.setStarsRating(starsRating);
            review.setReviewDate(reviewDate);
            review.setReviewContent(reviewContent);
            Reviews savedReview = reviewRepository.save(review);
            if (savedReview.getStarsRating() > 0) {
                Optional<OrderDetail> orderDetailOptional = orderDetailRepository.findLatestOrderByOrderId(orderId);
                if (orderDetailOptional.isPresent()) {
                    OrderDetail orderDetail = orderDetailOptional.get();
                    orderDetail.setIsReview(1);
                    orderDetailRepository.save(orderDetail);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseObject("ERROR", "order detail not found with ID: " + orderId, null));
                }
            }
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("OK", "Review added successfully", ""));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("ERROR", "An error occurred while processing the request", e.getMessage()));
        }
    }
}
