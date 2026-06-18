package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.Author;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.AuthorRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

  @Mock private AuthorRepository authorRepository;

  @InjectMocks private AuthorService authorService;

  private Author author;

  @BeforeEach
  void setUp() {
    author = Author.builder().id("a-1").firstName("Victor").lastName("Hugo").build();
  }

  @Test
  void getAll_returnsAllAuthors() {
    when(authorRepository.findAll()).thenReturn(List.of(author));

    List<Author> result = authorService.getAll();

    assertEquals(1, result.size());
    assertEquals("Victor", result.get(0).getFirstName());
  }

  @Test
  void getAll_emptyList_returnsEmpty() {
    when(authorRepository.findAll()).thenReturn(List.of());

    assertTrue(authorService.getAll().isEmpty());
  }

  @Test
  void getById_found() {
    when(authorRepository.findById("a-1")).thenReturn(Optional.of(author));

    Author result = authorService.getById("a-1");

    assertNotNull(result);
    assertEquals("a-1", result.getId());
  }

  @Test
  void getById_notFound_throwsNotFoundException() {
    when(authorRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> authorService.getById("unknown"));
  }

  @Test
  void crupdate_validAuthor_savesAndReturns() {
    when(authorRepository.save(any(Author.class))).thenReturn(author);

    Author result = authorService.crupdate("a-1", author);

    assertNotNull(result);
    verify(authorRepository).save(author);
  }

  @Test
  void crupdate_setsIdBeforeSave() {
    Author incoming = Author.builder().firstName("Emile").lastName("Zola").build();
    when(authorRepository.save(any(Author.class))).thenAnswer(inv -> inv.getArgument(0));

    Author result = authorService.crupdate("a-99", incoming);

    assertEquals("a-99", result.getId());
  }

  @Test
  void crupdate_nullFirstName_throwsBadRequest() {
    Author invalid = Author.builder().lastName("Hugo").build();

    assertThrows(BadRequestException.class, () -> authorService.crupdate("a-1", invalid));
    verify(authorRepository, never()).save(any());
  }

  @Test
  void crupdate_blankFirstName_throwsBadRequest() {
    Author invalid = Author.builder().firstName("   ").lastName("Hugo").build();

    assertThrows(BadRequestException.class, () -> authorService.crupdate("a-1", invalid));
    verify(authorRepository, never()).save(any());
  }

  @Test
  void crupdate_nullLastName_throwsBadRequest() {
    Author invalid = Author.builder().firstName("Victor").build();

    assertThrows(BadRequestException.class, () -> authorService.crupdate("a-1", invalid));
    verify(authorRepository, never()).save(any());
  }

  @Test
  void crupdate_blankLastName_throwsBadRequest() {
    Author invalid = Author.builder().firstName("Victor").lastName("").build();

    assertThrows(BadRequestException.class, () -> authorService.crupdate("a-1", invalid));
    verify(authorRepository, never()).save(any());
  }

  @Test
  void delete_existingAuthor_deletesAndReturns() {
    when(authorRepository.findById("a-1")).thenReturn(Optional.of(author));
    doNothing().when(authorRepository).deleteById("a-1");

    Author result = authorService.delete("a-1");

    assertEquals("a-1", result.getId());
    verify(authorRepository).deleteById("a-1");
  }

  @Test
  void delete_notFound_throwsNotFoundException() {
    when(authorRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> authorService.delete("unknown"));
    verify(authorRepository, never()).deleteById(any());
  }
}
