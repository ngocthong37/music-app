package com.vanvan.musicapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "listening_counts")
public class ListeningCount {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer songId;

    private Integer userId;

    private Date listenTime;

    private int count;
}