package com.vanvan.musicapp.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlbumResponse {

    private Integer id;
    private String title;
    private String coverImageUrl;
    private Date createdAt;
    private String genreName;
}
