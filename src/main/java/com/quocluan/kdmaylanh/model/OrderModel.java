package com.quocluan.kdmaylanh.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderModel {
    private Integer orderId;
    private Date orderDate;
    private Double totalPrice;
    private Integer totalQuantity;
    private String status;
    private Integer customerId;
    private String customerName;
}
