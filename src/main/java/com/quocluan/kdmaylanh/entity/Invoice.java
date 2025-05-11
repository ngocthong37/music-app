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
@Table(name = "Invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "InvoiceID")
    private Integer invoiceID;

    @Column(name = "Amount")
    private BigDecimal amount;

    @Column(name = "TransactionDate")
    private Date transactionDate;

    @ManyToOne
    @JoinColumn(name = "OrderID")
    private Orders orders;

    @Column(name = "Note")
    private String note;
}