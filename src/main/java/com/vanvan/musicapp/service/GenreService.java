package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Genre;
import com.vanvan.musicapp.entity.User;
import com.vanvan.musicapp.repository.GenreRepository;
import com.vanvan.musicapp.repository.UserRepository;
import com.vanvan.musicapp.request.CreateGenreRequest;
import com.vanvan.musicapp.response.GenreResponse;
import com.vanvan.musicapp.response.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    public ResponseObject getAllGenres() {
        try {
            List<GenreResponse> genreDTOs = genreRepository.findAll()
                    .stream()
                    .map(genre -> new GenreResponse(genre.getId(), genre.getName(), null))
                    .collect(Collectors.toList());
            return new ResponseObject("success", "Lấy danh sách thể loại thành công", genreDTOs);
        } catch (Exception e) {
            return new ResponseObject("error", "Lấy danh sách thể loại thất bại: " + e.getMessage(), null);
        }
    }

    public ResponseObject createGenre(CreateGenreRequest request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            return new ResponseObject("error", "User not found", null);
        }
        genre.setUser(optionalUser.get());

        Genre savedGenre = genreRepository.save(genre);
        return new ResponseObject("success", "Genre created", savedGenre);
    }
}