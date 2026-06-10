package com.library.hei.service;

import com.library.hei.model.entity.Genre;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

  @Mock private GenreRepository genreRepository;
  @InjectMocks private GenreService genreService;

  private Genre genre;

  @BeforeEach
  void setUp() {
    genre = Genre.builder().id("g-1").name("Roman").description("Littérature romanesque").build();
  }

  @Test
  void getAll_returnsAllGenres() {
    when(genreRepository.findAll()).thenReturn(List.of(genre));
    List<Genre> result = genreService.getAll();
    assertEquals(1, result.size());
    assertEquals("Roman", result.get(0).getName());
  }

  @Test
  void getById_found() {
    when(genreRepository.findById("g-1")).thenReturn(Optional.of(genre));
    Genre result = genreService.getById("g-1");
    assertEquals("g-1", result.getId());
  }

  @Test
  void getById_notFound_throwsNotFoundException() {
    when(genreRepository.findById("bad")).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> genreService.getById("bad"));
  }

  @Test
  void crupdate_validGenre_saves() {
    when(genreRepository.save(any(Genre.class))).thenReturn(genre);
    Genre result = genreService.crupdate("g-1", genre);
    assertNotNull(result);
    verify(genreRepository).save(genre);
  }

  @Test
  void crupdate_blankName_throwsBadRequest() {
    Genre invalid = Genre.builder().name("").build();
    assertThrows(BadRequestException.class, () -> genreService.crupdate("id", invalid));
    verify(genreRepository, never()).save(any());
  }

  @Test
  void delete_existing_deletesAndReturns() {
    when(genreRepository.findById("g-1")).thenReturn(Optional.of(genre));
    doNothing().when(genreRepository).deleteById("g-1");
    Genre result = genreService.delete("g-1");
    assertEquals("g-1", result.getId());
    verify(genreRepository).deleteById("g-1");
  }

  @Test
  void delete_notFound_throwsNotFoundException() {
    when(genreRepository.findById("bad")).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> genreService.delete("bad"));
    verify(genreRepository, never()).deleteById(any());
  }
}
