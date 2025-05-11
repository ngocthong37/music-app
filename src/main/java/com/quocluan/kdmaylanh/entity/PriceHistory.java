package com.quocluan.kdmaylanh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PriceHistory")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PriceHistoryID")
    private Integer priceHistoryID;

    @Column(name = "Price")
    private BigDecimal price;

    @Column(name = "CreatedAt")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "ProductID")
    private Product product;
}

