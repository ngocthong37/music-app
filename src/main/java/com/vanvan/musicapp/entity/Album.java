package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "albums")
public class Album {
    @Id
    private UUID albumId;

    @Column(length = 100)
    private String title;

    @ManyToOne
    private Artist artist;

    private Date createdAt;
}