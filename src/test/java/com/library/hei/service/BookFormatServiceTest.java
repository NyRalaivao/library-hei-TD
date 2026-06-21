package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.Book;
import com.library.hei.model.entity.BookFormat;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.BookFormatRepository;
import com.library.hei.repository.BookRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookFormatServiceTest {

  @Mock private BookFormatRepository bookFormatRepository;
  @Mock private BookRepository bookRepository;

  @InjectMocks private BookFormatService bookFormatService;

  private Book book;
  private Book otherBook;
  private BookFormat pocketFormat;
  private BookFormat hardcoverFormat;

  @BeforeEach
  void setUp() {
    book = Book.builder().id("book-1").title("Les Misérables").price(BigDecimal.TEN).build();
    otherBook =
        Book.builder().id("book-2").title("Notre-Dame de Paris").price(BigDecimal.TEN).build();

    pocketFormat =
        BookFormat.builder()
            .id("fmt-1")
            .book(book)
            .coverType(BookFormat.CoverType.POCKET)
            .price(new BigDecimal("9.90"))
            .stock(5)
            .build();

    hardcoverFormat =
        BookFormat.builder()
            .id("fmt-2")
            .book(book)
            .coverType(BookFormat.CoverType.HARDCOVER)
            .price(new BigDecimal("25.00"))
            .stock(3)
            .build();
  }

  @Test
  void getBookTotalStock_withValidBook_shouldReturnSumOfAllFormats() {
    when(bookRepository.existsById("book-1")).thenReturn(true);
    when(bookFormatRepository.findByBookId("book-1"))
        .thenReturn(List.of(pocketFormat, hardcoverFormat));

    int result = bookFormatService.getBookTotalStock("book-1");

    assertEquals(8, result); // 5 + 3
  }

  @Test
  void getBookTotalStock_withNoFormats_shouldReturnZero() {
    when(bookRepository.existsById("book-1")).thenReturn(true);
    when(bookFormatRepository.findByBookId("book-1")).thenReturn(List.of());

    assertEquals(0, bookFormatService.getBookTotalStock("book-1"));
  }

  @Test
  void getBookTotalStock_withNonExistingBook_shouldThrow404() {
    when(bookRepository.existsById("unknown")).thenReturn(false);

    assertThrows(NotFoundException.class, () -> bookFormatService.getBookTotalStock("unknown"));
    verify(bookFormatRepository, never()).findByBookId(any());
  }

  @Test
  void getBookCopyStock_withValidBookAndCopy_shouldReturnCorrectStock() {
    when(bookRepository.existsById("book-1")).thenReturn(true);
    when(bookFormatRepository.findById("fmt-1")).thenReturn(Optional.of(pocketFormat));

    int result = bookFormatService.getBookCopyStock("book-1", "fmt-1");

    assertEquals(5, result);
  }

  @Test
  void getBookCopyStock_withNonExistingCopy_shouldThrow404() {
    when(bookRepository.existsById("book-1")).thenReturn(true);
    when(bookFormatRepository.findById("fmt-unknown")).thenReturn(Optional.empty());

    assertThrows(
        NotFoundException.class, () -> bookFormatService.getBookCopyStock("book-1", "fmt-unknown"));
  }

  @Test
  void getBookCopyStock_withNonExistingBook_shouldThrow404() {
    when(bookRepository.existsById("unknown")).thenReturn(false);

    assertThrows(
        NotFoundException.class, () -> bookFormatService.getBookCopyStock("unknown", "fmt-1"));
    verify(bookFormatRepository, never()).findById(any());
  }

  @Test
  void getBookCopyStock_withCopyBelongingToAnotherBook_shouldThrow404() {
    // fmt-1 appartient à book-1, mais on le cherche via book-2
    when(bookRepository.existsById("book-2")).thenReturn(true);
    when(bookFormatRepository.findById("fmt-1")).thenReturn(Optional.of(pocketFormat));

    assertThrows(
        NotFoundException.class, () -> bookFormatService.getBookCopyStock("book-2", "fmt-1"));
  }

  @Test
  void hasEnoughStock_withSufficientStock_shouldReturnTrue() {
    when(bookFormatRepository.findById("fmt-1")).thenReturn(Optional.of(pocketFormat));

    assertTrue(bookFormatService.hasEnoughStock("fmt-1", 3)); // demande 3, stock 5
  }

  @Test
  void hasEnoughStock_withExactStock_shouldReturnTrue() {
    when(bookFormatRepository.findById("fmt-1")).thenReturn(Optional.of(pocketFormat));

    assertTrue(bookFormatService.hasEnoughStock("fmt-1", 5)); // demande 5, stock 5
  }

  @Test
  void hasEnoughStock_withInsufficientStock_shouldReturnFalse() {
    when(bookFormatRepository.findById("fmt-1")).thenReturn(Optional.of(pocketFormat));

    assertFalse(bookFormatService.hasEnoughStock("fmt-1", 10)); // demande 10, stock 5
  }

  @Test
  void hasEnoughStock_withNegativeQuantity_shouldThrowBadRequest() {
    assertThrows(BadRequestException.class, () -> bookFormatService.hasEnoughStock("fmt-1", -1));
    verify(bookFormatRepository, never()).findById(any());
  }

  @Test
  void hasEnoughStock_withNonExistingFormat_shouldThrow404() {
    when(bookFormatRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> bookFormatService.hasEnoughStock("unknown", 1));
  }

  @Test
  void getFormatsByBook_withValidBook_shouldReturnAllFormats() {
    when(bookRepository.existsById("book-1")).thenReturn(true);
    when(bookFormatRepository.findByBookId("book-1"))
        .thenReturn(List.of(pocketFormat, hardcoverFormat));

    List<BookFormat> result = bookFormatService.getFormatsByBook("book-1");

    assertEquals(2, result.size());
  }

  @Test
  void getFormatsByBook_withNonExistingBook_shouldThrow404() {
    when(bookRepository.existsById("unknown")).thenReturn(false);

    assertThrows(NotFoundException.class, () -> bookFormatService.getFormatsByBook("unknown"));
  }
}
