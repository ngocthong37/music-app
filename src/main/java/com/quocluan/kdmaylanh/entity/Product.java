package com.quocluan.kdmaylanh.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productID;

    private String name;

    private String indoorDimension;

    private Double indoorWeight;

    private String outdoorDimension;

    private Double outdoorWeight;

    private String heatCapacity;

    private String coolingCapacity;

    private Integer numberOfCooling;

    private Double powerComsumption;

    private Double price;
    private String indoorWarranty;

    private String outdoorWarranty;

    private String releaseDate;
    private Integer inventoryQuantity;

    private String radiatorMaterial;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "BrandID")
    private Brand brand;

    @JsonManagedReference
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> productImages;

    private String productStatus;
}