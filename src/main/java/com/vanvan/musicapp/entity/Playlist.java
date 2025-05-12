package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private User user;

    private String title;

    private Date createdAt;
}