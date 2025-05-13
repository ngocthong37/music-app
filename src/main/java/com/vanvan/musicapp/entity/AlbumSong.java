package com.vanvan.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "album_songs")
@IdClass(AlbumSongId.class)
@Getter
@Setter
public class AlbumSong {
    @Id
    private Integer albumId;

    @Id
    private Integer songId;
}