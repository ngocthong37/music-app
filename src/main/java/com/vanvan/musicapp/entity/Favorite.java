package com.vanvan.musicapp.entity;

import jakarta.persistence.Id;

import java.util.Date;

public class Favorite {
    @Id
    private Integer userId;

    @Id
    private Integer songId;

    private Date addedAt;
}
