package com.quocluan.kdmaylanh.entity;

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
@Table(name = "OrderStatus")
public class OrderStatus {

    @EmbeddedId
    private OrderStatusId id;

    @ManyToOne
    @JoinColumn(name = "StatusID")
    private Status status;


    @Column(name = "EmployeeID")
    private Integer employeeID;

    @Column(name = "Note")
    private String note;
}
