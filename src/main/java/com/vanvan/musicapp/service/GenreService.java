package com.vanvan.musicapp.service;

import com.vanvan.musicapp.entity.Genre;
import com.vanvan.musicapp.repository.GenreRepository;
import com.vanvan.musicapp.request.CreateGenreRequest;
import com.vanvan.musicapp.response.GenreResponse;
import com.vanvan.musicapp.response.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

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
        Genre savedGenre = genreRepository.save(genre);
        return new ResponseObject("success", "Genre created", savedGenre);
    }
}