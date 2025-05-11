package com.vanvan.musicapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    private UUID playlistId;

    @ManyToOne
    private User user;

    private String title;

    private Date createdAt;
}