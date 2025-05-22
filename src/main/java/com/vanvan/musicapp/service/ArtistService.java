package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Artist;
import com.vanvan.musicapp.repository.ArtistRepository;
import com.vanvan.musicapp.request.CreateArtistRequest;
import com.vanvan.musicapp.response.ArtistResponse;
import com.vanvan.musicapp.response.ResponseObject;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistService {
    private ArtistRepository artistRepository;
    private StorageService storageService;

    public ResponseObject getAllArtists() {
        List<ArtistResponse> artistDTOs = artistRepository.findAll()
                .stream()
                .map(artist -> new ArtistResponse(artist.getId(), artist.getName(), artist.getImageUrl(), null))
                .collect(Collectors.toList());
        Map<String, Object> result = new HashMap<>();
        result.put("artists", artistDTOs);
        return new ResponseObject("success", "Artists retrieved", artistDTOs);
    }

    public ResponseObject createArtist(CreateArtistRequest request) {
        Artist artist = new Artist();
        artist.setName(request.getName());
        Artist savedArtist = artistRepository.save(artist);
        return new ResponseObject("success", "Artist created", savedArtist);
    }

    public String uploadImage(MultipartFile file, String namePath, Integer songId) {
        String imageUrl = storageService.uploadImages(file, namePath);
        artistRepository.updateImage(imageUrl, songId);
        return imageUrl;
    }

}