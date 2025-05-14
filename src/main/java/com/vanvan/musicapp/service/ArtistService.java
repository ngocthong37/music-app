package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Artist;
import com.vanvan.musicapp.repository.ArtistRepository;
import com.vanvan.musicapp.request.CreateArtistRequest;
import com.vanvan.musicapp.response.ArtistResponse;
import com.vanvan.musicapp.response.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    public ResponseObject getAllArtists() {
        List<ArtistResponse> artistDTOs = artistRepository.findAll()
                .stream()
                .map(artist -> new ArtistResponse(artist.getId(), artist.getName()))
                .collect(Collectors.toList());
        return new ResponseObject("success", "Artists retrieved", artistDTOs);
    }

    public ResponseObject createArtist(CreateArtistRequest request) {
        Artist artist = new Artist();
        artist.setName(request.getName());
        Artist savedArtist = artistRepository.save(artist);
        return new ResponseObject("success", "Artist created", savedArtist);
    }
}