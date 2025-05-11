package com.quocluan.kdmaylanh.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmployeeID")
    private Integer employeeID;

    private String name;

    private LocalDate birthDate;

    @Column(name = "IdentityNumber")
    private String identityNumber;

    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @Column(name = "Email")
    private String email;

    @Column(name = "Gender")
    private String gender;

    @Column(name = "Address")
    private String address;

    @Column(name = "HireDate")
    private Date hireDate;

    @Column(name = "EmployeeStatus")
    private String employeeStatus;

    @JsonBackReference
    @OneToOne(mappedBy = "employee")
    private Account account;

}