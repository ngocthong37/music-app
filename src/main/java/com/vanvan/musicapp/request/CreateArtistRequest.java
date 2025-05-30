package com.vanvan.musicapp.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateArtistRequest {
    private String name;
    private String bio;
    private Integer userId;
}
