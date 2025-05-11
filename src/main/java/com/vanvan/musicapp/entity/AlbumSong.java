package com.vanvan.musicapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "album_songs")
@IdClass(AlbumSongId.class)
public class AlbumSong {
    @Id
    private UUID albumId;

    @Id
    private UUID songId;
}