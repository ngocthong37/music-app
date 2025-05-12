package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "album_songs")
@IdClass(AlbumSongId.class)
public class AlbumSong {
    @Id
    private Integer albumId;

    @Id
    private Integer songId;
}