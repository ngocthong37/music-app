package com.vanvan.musicapp.response;


import com.vanvan.musicapp.entity.Song;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FavoriteResponse {
    private Integer id;
    private List<SongResponse> songs;
    private Date createdAt;
}
