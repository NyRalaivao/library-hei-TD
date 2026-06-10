package com.library.hei.service;

import com.library.hei.model.entity.Genre;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.GenreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class GenreService {
  private final GenreRepository genreRepository;

  public List<Genre> getAll() { return genreRepository.findAll(); }

  public Genre getById(String id) {
    return genreRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Genre id=" + id + " introuvable"));
  }

  public Genre crupdate(String id, Genre genre) {
    if (genre.getName() == null || genre.getName().isBlank())
      throw new BadRequestException("Le nom du genre est obligatoire");
    genre.setId(id);
    return genreRepository.save(genre);
  }

  public Genre delete(String id) {
    Genre genre = getById(id);
    genreRepository.deleteById(id);
    return genre;
  }
}
