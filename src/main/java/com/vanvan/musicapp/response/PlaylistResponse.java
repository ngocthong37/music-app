package com.vanvan.musicapp.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class PlaylistResponse {
    private Integer id;
    private String title;
    private Integer userId;
    private Date createdAt;
    private List<SongResponse> songs;
}