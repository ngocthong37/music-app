package com.vanvan.musicapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "genres")
@Getter
@Setter
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String name; // Tên thể loại

    @OneToMany(mappedBy = "genre")
    private List<Album> albums; // Mối quan hệ với Album
}
