package com.vanvan.musicapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "artists")
@Getter
@Setter
public class Artist {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(length = 100)
    private String name;

    @Lob
    private String bio;

    private String imageUrl;

    @OneToMany(mappedBy = "artist")
    private List<Song> songs;

}