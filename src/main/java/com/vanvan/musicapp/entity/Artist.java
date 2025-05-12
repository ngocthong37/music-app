package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 100)
    private String name;

    @Lob
    private String bio;
}