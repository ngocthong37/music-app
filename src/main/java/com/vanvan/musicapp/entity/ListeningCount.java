package com.vanvan.musicapp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "listening_counts", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "songId"}))
@Getter
@Setter
public class ListeningCount {
    @Id
    @GeneratedValue
    private Integer id;

    private Integer songId;

    private Integer userId;

    private Date listenTime;

    private int count;

    @PrePersist
    @PreUpdate
    public void updateListenTime() {
        this.listenTime = new Date();
    }
}