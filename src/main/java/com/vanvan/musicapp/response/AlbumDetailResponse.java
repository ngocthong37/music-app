package com.vanvan.musicapp.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class AlbumDetailResponse {
    private Integer id;
    private String title;
    private String coverImageUrl;
    private Date createdAt;
    private String genreName;
    private List<SongResponse> songs;
}