package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "genres")
public class Genre {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 50)
    private String name;
}
