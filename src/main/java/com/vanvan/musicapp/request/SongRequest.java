package com.vanvan.musicapp.request;

import lombok.Data;

import java.util.UUID;

@Data
public class SongRequest {
    private String title;
    private Integer artistId;
    private Integer genreId;
    private int duration;
    private String fileUrl;
    private String lyrics;
}
