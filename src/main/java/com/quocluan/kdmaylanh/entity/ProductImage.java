package com.quocluan.kdmaylanh.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ProductImage")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProImageID")
    private Integer proImageID;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "ProductID")
    private Product product;

    @Column(name = "ImagePath")
    private String imagePath;

    @Column(name = "IsAvatar")
    private Boolean isAvatar;
}