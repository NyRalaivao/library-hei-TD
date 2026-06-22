package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.Arrival;
import com.library.hei.model.entity.Book;
import com.library.hei.model.entity.BookFormat;
import com.library.hei.model.entity.StockMovement;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.ArrivalRepository;
import com.library.hei.repository.BookFormatRepository;
import com.library.hei.repository.BookRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArrivalServiceTest {

  @Mock private ArrivalRepository arrivalRepository;
  @Mock private BookRepository bookRepository;
  @Mock private BookFormatRepository bookFormatRepository;
  // NOUVEAU : on mocke StockMovementService pour vérifier qu'il est appelé
  // sans avoir besoin qu'il fasse vraiment la persistance
  @Mock private StockMovementService stockMovementService;

  @InjectMocks private ArrivalService arrivalService;

  private Book book;
  private BookFormat format;
  private Arrival arrival;

  @BeforeEach
  void setUp() {
    book = Book.builder().id("book-1").title("Clean Code").build();
    format = BookFormat.builder().id("format-1").book(book).stock(10).build();
    arrival = Arrival.builder().book(book).arrivalDate(LocalDate.now()).quantity(5).build();
  }

  @Test
  void getAll_returnsAllArrivals() {
    when(arrivalRepository.findAll()).thenReturn(List.of(arrival));

    List<Arrival> result = arrivalService.getAll();

    assertEquals(1, result.size());
    verify(arrivalRepository).findAll();
  }

  @Test
  void getByBook_returnsBookArrivals() {
    when(arrivalRepository.findByBookIdOrderByArrivalDateDesc("book-1"))
        .thenReturn(List.of(arrival));

    List<Arrival> result = arrivalService.getByBook("book-1");

    assertEquals(1, result.size());
    verify(arrivalRepository).findByBookIdOrderByArrivalDateDesc("book-1");
  }

  @Test
  void createArrival_validArrival_savesAndUpdatesStock() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.of(book));
    when(bookFormatRepository.findById("format-1")).thenReturn(Optional.of(format));
    when(arrivalRepository.save(arrival)).thenReturn(arrival);

    Arrival result = arrivalService.createArrival(arrival, "format-1");

    assertNotNull(result);
    assertEquals(15, format.getStock()); // 10 + 5
    verify(bookFormatRepository).save(format);
    verify(arrivalRepository).save(arrival);
  }

  // NOUVEAU : vérifie que le mouvement de stock est bien enregistré lors d'un arrivage
  @Test
  void createArrival_recordsStockMovementOfTypeArrival() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.of(book));
    when(bookFormatRepository.findById("format-1")).thenReturn(Optional.of(format));
    when(arrivalRepository.save(arrival)).thenReturn(arrival);

    arrivalService.createArrival(arrival, "format-1");

    // On vérifie que record() a été appelé avec les bons paramètres
    verify(stockMovementService)
        .record(
            eq(format),
            eq(StockMovement.MovementType.ARRIVAL), // type correct
            eq(5), // quantité positive (entrée de stock)
            any() // referenceId = id de l'arrivage sauvé
            );
  }

  @Test
  void createArrival_quantityNull_throwsBadRequestException() {
    arrival.setQuantity(null);

    assertThrows(
        BadRequestException.class, () -> arrivalService.createArrival(arrival, "format-1"));

    verify(arrivalRepository, never()).save(any());
    // Aucun mouvement de stock ne doit être créé en cas d'erreur
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }

  @Test
  void createArrival_quantityZero_throwsBadRequestException() {
    arrival.setQuantity(0);

    assertThrows(
        BadRequestException.class, () -> arrivalService.createArrival(arrival, "format-1"));

    verify(arrivalRepository, never()).save(any());
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }

  @Test
  void createArrival_bookNotFound_throwsNotFoundException() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> arrivalService.createArrival(arrival, "format-1"));

    verify(arrivalRepository, never()).save(any());
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }

  @Test
  void createArrival_formatNotFound_throwsNotFoundException() {
    when(bookRepository.findById("book-1")).thenReturn(Optional.of(book));
    when(bookFormatRepository.findById("format-1")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> arrivalService.createArrival(arrival, "format-1"));

    verify(arrivalRepository, never()).save(any());
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }
}
