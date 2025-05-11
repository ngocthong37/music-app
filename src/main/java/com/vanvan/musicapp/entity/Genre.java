package com.vanvan.musicapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "genres")
public class Genre {
    @Id
    private UUID genreId;

    @Column(length = 50)
    private String name;
}
