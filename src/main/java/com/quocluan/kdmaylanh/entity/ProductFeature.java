package com.quocluan.kdmaylanh.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ProductFeature")
public class ProductFeature {
    @EmbeddedId
    private ProductFeatureId id;

    @ManyToOne
    @MapsId("productID")
    @JoinColumn(name = "ProductID")
    private Product product;

    @ManyToOne
    @MapsId("featureID")
    @JoinColumn(name = "FeatureID")
    private Feature feature;

    @Column(name = "Description")
    private String description;
}