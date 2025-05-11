package com.quocluan.kdmaylanh.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountModel {
    private Integer accountID;
    private String phoneNumber;
    private String email;
    private String password;
    private String name;
}
