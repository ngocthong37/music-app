package com.vanvan.musicapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/v1/music")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MusicController {

    @GetMapping(value = "/{fileName}", produces = "audio/mpeg")
    public ResponseEntity<FileSystemResource> getMusic(@PathVariable String fileName) {
        File file = new File("uploads/" + fileName);
        if (file.exists()) {
            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(new FileSystemResource(file));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
