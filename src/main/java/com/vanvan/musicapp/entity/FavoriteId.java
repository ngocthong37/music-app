package com.vanvan.musicapp.entity;

import java.io.Serializable;
import java.util.UUID;

public class FavoriteId implements Serializable {
    private UUID userId;
    private UUID songId;
}
