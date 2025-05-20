package com.vanvan.musicapp.utils;

import com.vanvan.musicapp.entity.Song;
import com.vanvan.musicapp.response.SongResponse;
import org.springframework.stereotype.Component;

@Component
public class UtilsService {
    public SongResponse convertToSongResponse(Song song) {
        if (song == null) return null;

        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getArtist() != null ? song.getArtist().getId() : null,
                song.getArtist() != null ? song.getArtist().getName() : null,
                song.getDuration(),
                song.getFileUrl(),
                song.getImageUrl(),
                song.getGenre() != null ? song.getGenre().getId() : null,
                song.getGenre() != null ? song.getGenre().getName() : null
        );
    }
}
