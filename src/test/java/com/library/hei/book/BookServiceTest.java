package com.library.hei.book;

import com.library.hei.model.entity.Book;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.BookRepository;
import com.library.hei.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

  @Mock
  private BookRepository bookRepository;

  @InjectMocks
  private BookService bookService;

  private Book sampleBook;

  @BeforeEach
  void setUp() {
    sampleBook = Book.builder()
        .id("book-1")
        .title("Clean Code")
        .author("Robert Martin")
        .pageNumbers(431)
        .format(Book.BookFormat.OTHER)
        .topic("Programming")
        .creationDatetime(Instant.now())
        .build();
  }

  @Test
  void getBookById_ok() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.of(sampleBook));

    Book result = bookService.getBookById("book-1");

    assertNotNull(result);
    assertEquals("book-1", result.getId());
    assertEquals("Clean Code", result.getTitle());
    verify(bookRepository).findById("book-1");
  }

  @Test
  void getBookById_notFound_throwsException() {
    when(bookRepository.findById("unknown")).thenReturn(Optional.empty());

    NotFoundException ex = assertThrows(NotFoundException.class,
        () -> bookService.getBookById("unknown"));

    assertTrue(ex.getMessage().contains("unknown"));
  }

  @Test
  void getBooks_withFilters_ok() {
    when(bookRepository.findByFilters(anyString(), any(), any(), any()))
        .thenReturn(List.of(sampleBook));

    List<Book> result = bookService.getBooks(0, 10, "Clean", null, null);

    assertEquals(1, result.size());
    verify(bookRepository).findByFilters(eq("Clean"), isNull(), isNull(), any(PageRequest.class));
  }

  @Test
  void crupdateBook_ok() {
    when(bookRepository.save(any(Book.class))).thenReturn(sampleBook);

    Book result = bookService.crupdateBook("book-1", sampleBook);

    assertNotNull(result);
    verify(bookRepository).save(sampleBook);
  }

  @Test
  void crupdateBook_missingTitle_throwsBadRequest() {
    Book invalid = Book.builder()
        .author("Someone")
        .format(Book.BookFormat.NOVEL)
        .build();

    assertThrows(BadRequestException.class,
        () -> bookService.crupdateBook("id", invalid));
    verify(bookRepository, never()).save(any());
  }

  @Test
  void crupdateBook_missingAuthor_throwsBadRequest() {
    Book invalid = Book.builder()
        .title("Some Title")
        .format(Book.BookFormat.MANGA)
        .build();

    BadRequestException ex = assertThrows(BadRequestException.class,
        () -> bookService.crupdateBook("id", invalid));
    assertTrue(ex.getMessage().toLowerCase().contains("author"));
  }

  @Test
  void crupdateBook_negativePageNumbers_throwsBadRequest() {
    Book invalid = Book.builder()
        .title("Some Title")
        .author("Author")
        .format(Book.BookFormat.COMIC)
        .pageNumbers(-5)
        .build();

    assertThrows(BadRequestException.class,
        () -> bookService.crupdateBook("id", invalid));
  }

  @Test
  void importBook_ok() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.of(sampleBook));
    when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

    Book copy = bookService.importBook("book-1");

    assertNotNull(copy);
    assertNotEquals("book-1", copy.getId());
    assertTrue(copy.getTitle().contains("Copy of"));
    assertEquals(sampleBook.getAuthor(), copy.getAuthor());
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void importBook_notFound_throwsException() {
    when(bookRepository.findById("missing")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookService.importBook("missing"));
  }

  @Test
  void deleteBook_ok() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.of(sampleBook));
    doNothing().when(bookRepository).deleteById("book-1");

    Book deleted = bookService.deleteBook("book-1");

    assertEquals("book-1", deleted.getId());
    verify(bookRepository).deleteById("book-1");
  }

  @Test
  void deleteBook_notFound_throwsException() {
    when(bookRepository.findById("missing")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookService.deleteBook("missing"));
  }
}
