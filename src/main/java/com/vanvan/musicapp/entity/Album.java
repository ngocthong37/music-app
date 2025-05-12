package com.vanvan.musicapp.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 100)
    private String title;

    @ManyToOne
    private Artist artist;

    private Date createdAt;
}