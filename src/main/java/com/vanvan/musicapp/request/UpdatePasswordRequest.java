package com.vanvan.musicapp.request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private Integer id;
    private String currentPassword;
    private String newPassword;
}