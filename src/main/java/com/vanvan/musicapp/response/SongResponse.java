package com.vanvan.musicapp.response;


import lombok.AllArgsConstructor;
import lombok.Data;

    @Data
    @AllArgsConstructor
    public class SongResponse {
        private Integer id;
        private String title;
        private String artistName;
        private int duration;
        private String fileUrl;
    }
