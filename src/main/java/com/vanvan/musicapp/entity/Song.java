package com.vanvan.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "songs")
@Setter
@Getter
public class Song {
    @Id
    @GeneratedValue
    private Integer id;

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