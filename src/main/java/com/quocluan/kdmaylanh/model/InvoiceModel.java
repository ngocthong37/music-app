package com.quocluan.kdmaylanh.model;

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
public class InvoiceModel {
    private Integer invoiceId;
    private Integer orderId;
    private Date transactionDate;
    private BigDecimal amount;
    private String note;
}
