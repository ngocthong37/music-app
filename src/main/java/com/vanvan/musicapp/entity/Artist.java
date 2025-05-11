package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "artists")
public class Artist {
    @Id
    private UUID artistId;

    @Column(length = 100)
    private String name;

    @Lob
    private String bio;
}