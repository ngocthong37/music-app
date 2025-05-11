package com.quocluan.kdmaylanh.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItemModel {
    private Integer productId;
    private String productName;
    private String productUrl;
    private Integer quantity;
    private Double price;
}
