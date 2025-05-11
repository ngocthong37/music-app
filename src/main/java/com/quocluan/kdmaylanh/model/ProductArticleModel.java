package com.quocluan.kdmaylanh.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductArticleModel{
    private Integer articleID;
    private String productName;
    private String arHeading;
    private String arContent;
    private String employeeName;
    private Date createdDate;
}
