package com.vanvan.musicapp.entity;

import jakarta.persistence.Id;

import java.util.Date;
import java.util.UUID;

public class Favorite {
    @Id
    private UUID userId;

    @Id
    private UUID songId;

    private Date addedAt;
}
