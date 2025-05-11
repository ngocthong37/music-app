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
@Table(name = "Address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressID")
    private Integer addressID;

    @Column(name = "CityID")
    private String cityID;

    @Column(name = "DistrictID")
    private String districtID;

    @Column(name = "WardID")
    private String wardID;

    @Column(name = "DetailAddress")
    private String detailAddress;

    @Column(name = "IsDefault")
    private Integer isDefault;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "CustomerID")
    private Customer customer;
}