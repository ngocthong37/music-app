package com.vanvan.musicapp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtistResponse {
    private Integer id;
    private String name;
    private Long listenCount;
}