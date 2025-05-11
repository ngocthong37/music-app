package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "songs")
public class Song {
    @Id
    private UUID songId;

    @Column(length = 100)
    private String title;

    @ManyToOne
    private Artist artist;

    @ManyToOne
    private Genre genre;

    private int duration;

    private String fileUrl;

    @Lob
    private String lyrics;

    private Date createdAt;
}