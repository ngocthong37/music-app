package com.vanvan.musicapp.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArtistResponse {
    private Integer id;
    private String name;
    private String imageUrl;
    private Long listenCount;
}