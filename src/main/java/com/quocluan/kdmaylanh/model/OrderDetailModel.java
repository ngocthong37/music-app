package com.quocluan.kdmaylanh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDetailModel {
    private Integer orderId;
    private Integer totalQuantity;
    private String status;
    private Double totalMoney;
    private Date orderDate;
    private List<OrderItemModel> orderItems;
}
