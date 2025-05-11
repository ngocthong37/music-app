package com.quocluan.kdmaylanh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Reviews")
public class Reviews {
    @EmbeddedId
    private ReviewsId id;

    @ManyToOne
    @MapsId("productID")
    @JoinColumn(name = "ProductID")
    private Product product;

    @ManyToOne
    @MapsId("orderID")
    @JoinColumn(name = "OrderID")
    private Orders orders;

    @Column(name = "ReviewContent")
    private String reviewContent;

    @Column(name = "ReviewDate")
    private Date reviewDate;

    @Column(name = "StarsRating")
    private Integer starsRating;
}