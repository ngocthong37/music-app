package com.vanvan.musicapp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreResponse {
    private Integer id;
    private String name;
    private Long favoriteCount;
}