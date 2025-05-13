package com.vanvan.musicapp.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateAlbumRequest {
    private String title;
    private Integer genreId;
    private String coverImageUrl;
    private List<Integer> songIds;
}
