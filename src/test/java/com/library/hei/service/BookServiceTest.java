package com.library.hei.service;

import com.library.hei.model.entity.Book;
import com.library.hei.model.entity.BookFormat;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.AuthorRepository;
import com.library.hei.repository.BookRepository;
import com.library.hei.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock private BookRepository bookRepository;
  @Mock private GenreRepository genreRepository;
  @Mock private AuthorRepository authorRepository;

  @InjectMocks
  private BookService bookService;

  private Book book;

  @BeforeEach
  void setUp() {
    book = Book.builder()
        .id("book-1")
        .title("Les Misérables")
        .isbn("9782070360024")
        .price(new BigDecimal("12.50"))
        .build();
  }

  // ─── getAll ───────────────────────────────────────────────────────────────

  @Test
  void getAll_returnsPageContent() {
    Page<Book> page = new PageImpl<>(List.of(book));
    when(bookRepository.findAll(any(PageRequest.class))).thenReturn(page);

    List<Book> result = bookService.getAll(0, 10);

    assertEquals(1, result.size());
    assertEquals("Les Misérables", result.get(0).getTitle());
  }

  @Test
  void getAll_emptyPage_returnsEmptyList() {
    when(bookRepository.findAll(any(PageRequest.class))).thenReturn(Page.empty());
    assertTrue(bookService.getAll(0, 10).isEmpty());
  }

  // ─── getById ──────────────────────────────────────────────────────────────

  @Test
  void getById_found() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.of(book));

    Book result = bookService.getById("book-1");

    assertNotNull(result);
    assertEquals("book-1", result.getId());
  }

  @Test
  void getById_notFound_throwsNotFoundException() {
    when(bookRepository.findById("unknown")).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> bookService.getById("unknown"));
  }

  // ─── getByIsbn ────────────────────────────────────────────────────────────

  @Test
  void getByIsbn_found() {
    when(bookRepository.findByIsbn("9782070360024")).thenReturn(Optional.of(book));

    Book result = bookService.getByIsbn("9782070360024");
    assertEquals("9782070360024", result.getIsbn());
  }

  @Test
  void getByIsbn_notFound_throwsNotFoundException() {
    when(bookRepository.findByIsbn("0000000000")).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> bookService.getByIsbn("0000000000"));
  }

  // ─── crupdate ─────────────────────────────────────────────────────────────

  @Test
  void crupdate_validBook_savesAndReturns() {
    when(bookRepository.save(any(Book.class))).thenReturn(book);

    Book result = bookService.crupdate("book-1", book);

    assertNotNull(result);
    verify(bookRepository).save(book);
  }

  @Test
  void crupdate_missingTitle_throwsBadRequest() {
    Book invalid = Book.builder().price(BigDecimal.TEN).build();
    assertThrows(BadRequestException.class, () -> bookService.crupdate("id", invalid));
    verify(bookRepository, never()).save(any());
  }

  @Test
  void crupdate_blankTitle_throwsBadRequest() {
    Book invalid = Book.builder().title("   ").price(BigDecimal.TEN).build();
    assertThrows(BadRequestException.class, () -> bookService.crupdate("id", invalid));
  }

  @Test
  void crupdate_negativePrice_throwsBadRequest() {
    Book invalid = Book.builder().title("Un titre").price(new BigDecimal("-5")).build();
    assertThrows(BadRequestException.class, () -> bookService.crupdate("id", invalid));
  }

  // ─── delete ───────────────────────────────────────────────────────────────

  @Test
  void delete_existingBook_deletesAndReturns() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.of(book));
    doNothing().when(bookRepository).deleteById("book-1");

    Book result = bookService.delete("book-1");

    assertEquals("book-1", result.getId());
    verify(bookRepository).deleteById("book-1");
  }

  @Test
  void delete_notFound_throwsNotFoundException() {
    when(bookRepository.findById("unknown")).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> bookService.delete("unknown"));
    verify(bookRepository, never()).deleteById(any());
  }

  // ─── searchByTitle ────────────────────────────────────────────────────────

  @Test
  void searchByTitle_returnsMatchingBooks() {
    when(bookRepository.findByTitleStartingWith(eq("Les"), any(PageRequest.class)))
        .thenReturn(List.of(book));

    List<Book> result = bookService.searchByTitle("Les", 0, 10);

    assertEquals(1, result.size());
    assertTrue(result.get(0).getTitle().startsWith("Les"));
  }

  @Test
  void searchByTitle_noMatch_returnsEmpty() {
    when(bookRepository.findByTitleStartingWith(eq("ZZZ"), any(PageRequest.class)))
        .thenReturn(List.of());
    assertTrue(bookService.searchByTitle("ZZZ", 0, 10).isEmpty());
  }
}
