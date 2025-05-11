package com.quocluan.kdmaylanh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderedConfirmedModel {
    private Integer orderId;
    private Date orderDate;
    private Integer employeeID;
    private String status;
    private Integer customerId;
    private String employeeName;
}
