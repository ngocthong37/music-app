package com.vanvan.musicapp.response;

import lombok.Data;

import java.util.Date;

@Data
public class ListeningCountResponse {
    private Integer id;
    private SongResponse song;
    private int count;
    private Date listenTime;
}