package com.vanvan.musicapp.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class PlaylistCreateRequest {
    @NotBlank
    private String title;
    private Integer userId;
    private List<Integer> songIds;
}