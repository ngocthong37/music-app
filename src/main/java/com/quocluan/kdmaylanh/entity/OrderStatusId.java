package com.quocluan.kdmaylanh.entity;

import com.quocluan.kdmaylanh.entity.Orders;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class OrderStatusId implements Serializable {

    @ManyToOne
    @JoinColumn(name = "OrderID")
    private Orders orders;

    @Column(name = "DateUpdate")
    private Date dateUpdate;
}
