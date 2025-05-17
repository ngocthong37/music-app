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
    }
