package com.vanvan.musicapp.request;

import lombok.Data;

@Data
public class FavoriteRequest {
    private Integer userId;
    private Integer songId;
}
