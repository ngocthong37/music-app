package com.quocluan.kdmaylanh.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ReviewsId implements Serializable {
    private Integer productID;
    private Integer orderID;

    public ReviewsId() {

    }

    public ReviewsId(Integer productID, int orderID) {

    }

}
