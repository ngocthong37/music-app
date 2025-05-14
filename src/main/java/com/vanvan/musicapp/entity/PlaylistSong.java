package com.vanvan.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "playlist_songs")
@IdClass(PlaylistSongId.class)
@Setter
@Getter
public class PlaylistSong {
    @Id
    private Integer playlistId;

    @Id
    private Integer songId;

    @Column(name = "order_index")
    private int orderIndex;
}
