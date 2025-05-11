package com.vanvan.musicapp.entity;

import java.io.Serializable;
import java.util.UUID;

public class AlbumSongId implements Serializable {
    private UUID albumId;
    private UUID songId;
}