package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "playlist_songs")
@IdClass(PlaylistSongId.class)
public class PlaylistSong {
    @Id
    private Integer playlistId;

    @Id
    private Integer songId;

    @Column(name = "order_index")
    private int orderIndex;
}
