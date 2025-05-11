package com.quocluan.kdmaylanh.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ProductFeatureId implements Serializable {
    private int productID;
    private int featureID;

    public ProductFeatureId() {

    }
    public ProductFeatureId(int productID, int featureId) {
    }
}
