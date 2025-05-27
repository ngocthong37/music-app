package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Artist;
import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.ArtistRepository;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.request.CreateArtistRequest;
import com.vanvan.musicapp.response.ArtistResponse;
import com.vanvan.musicapp.response.ResponseObject;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistService {
    private ArtistRepository artistRepository;
    private StorageService storageService;
    private UserRepository userRepository;

    public ResponseObject getAllArtists() {
        List<ArtistResponse> artistDTOs = artistRepository.findAll()
                .stream()
                .map(artist -> new ArtistResponse(artist.getId(), artist.getName(), artist.getImageUrl(), null,artist.getSongs().size(), artist.getBio(), artist.getCreatedAt()))
                .collect(Collectors.toList());
        Collections.reverse(artistDTOs);
        Map<String, Object> result = new HashMap<>();
        result.put("artists", artistDTOs);
        return new ResponseObject("success", "Artists retrieved", artistDTOs);
    }

    public ResponseObject createArtist(CreateArtistRequest request) {
        Artist artist = new Artist();
        artist.setName(request.getName());
        artist.setBio(request.getBio()); // set bio
        artist.setCreatedAt(new Date());

        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            return new ResponseObject("error", "User not found", null);
        }
        artist.setUser(optionalUser.get());

        Artist savedArtist = artistRepository.save(artist);
        return new ResponseObject("success", "Artist created", savedArtist);
    }


    public String uploadImage(MultipartFile file, String namePath, Integer songId) {
        String imageUrl = storageService.uploadImages(file, namePath);
        artistRepository.updateImage(imageUrl, songId);
        return imageUrl;
    }

    public ResponseObject updateArtist(Integer id, CreateArtistRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));
        artist.setName(request.getName());
        artist.setBio(request.getBio());
        artist.setUpdatedAt(new Date());
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            return new ResponseObject("error", "User not found", null);
        }
        artist.setUser(optionalUser.get());

        Artist updatedArtist = artistRepository.save(artist);
        return new ResponseObject("success", "Artist updated", updatedArtist);
    }

    public ResponseObject deleteArtist(Integer id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Artist not found"));
        if (!artist.getSongs().isEmpty()) {
            throw new IllegalStateException("Cannot delete artist with associated songs");
        }
        artistRepository.delete(artist);
        return new ResponseObject("success", "Artist deleted", null);
    }

}