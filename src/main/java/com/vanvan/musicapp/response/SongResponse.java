package com.vanvan.musicapp.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongResponse {
    private Integer id;
    private String title;
    private Integer artistId;
    private String artistName;
    private int duration;
    private String fileUrl;
    private String imageUrl;
    private Integer genreId;
    private String genreName;
    private Long listenCount;
    private Long favoriteCount;

    public SongResponse(Integer id, String title, Integer artistId, String artistName, int duration, String fileUrl, String imageUrl, Integer genreId, String genreName) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.artistName = artistName;
        this.duration = duration;
        this.fileUrl = fileUrl;
        this.imageUrl = imageUrl;
        this.genreId = genreId;
        this.genreName = genreName;
    }
}