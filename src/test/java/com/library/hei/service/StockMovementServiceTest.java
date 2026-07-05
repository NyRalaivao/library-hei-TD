package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.BookFormat;
import com.library.hei.model.entity.StockMovement;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.BookFormatRepository;
import com.library.hei.repository.StockMovementRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceTest {

  @Mock private StockMovementRepository stockMovementRepository;
  @Mock private BookFormatRepository bookFormatRepository;
  @InjectMocks private StockMovementService stockMovementService;

  private BookFormat format;
  private StockMovement movement;

  @BeforeEach
  void setUp() {
    format = BookFormat.builder().id("f-1").stock(5).build();
    movement =
        StockMovement.builder()
            .id("m-1")
            .bookFormat(format)
            .type(StockMovement.MovementType.ARRIVAL)
            .quantity(10)
            .referenceId("arrival-1")
            .build();
  }

  @Test
  void getHistoryByFormat_existingFormat_returnsHistory() {
    when(bookFormatRepository.existsById("f-1")).thenReturn(true);
    when(stockMovementRepository.findByBookFormatIdOrderByMovementDateDesc("f-1"))
        .thenReturn(List.of(movement));

    List<StockMovement> result = stockMovementService.getHistoryByFormat("f-1");

    assertEquals(1, result.size());
    assertEquals("m-1", result.get(0).getId());
  }

  @Test
  void getHistoryByFormat_unknownFormat_throwsNotFoundException() {
    when(bookFormatRepository.existsById("bad")).thenReturn(false);

    assertThrows(NotFoundException.class, () -> stockMovementService.getHistoryByFormat("bad"));
    verify(stockMovementRepository, never()).findByBookFormatIdOrderByMovementDateDesc(any());
  }

  @Test
  void getByReference_returnsMatchingMovements() {
    when(stockMovementRepository.findByReferenceId("arrival-1")).thenReturn(List.of(movement));

    List<StockMovement> result = stockMovementService.getByReference("arrival-1");

    assertEquals(1, result.size());
    assertEquals("arrival-1", result.get(0).getReferenceId());
  }

  @Test
  void calculateCurrentStock_existingFormat_returnsSum() {
    when(bookFormatRepository.existsById("f-1")).thenReturn(true);
    when(stockMovementRepository.calculateStockByFormatId("f-1")).thenReturn(8);

    int result = stockMovementService.calculateCurrentStock("f-1");

    assertEquals(8, result);
  }

  @Test
  void calculateCurrentStock_unknownFormat_throwsNotFoundException() {
    when(bookFormatRepository.existsById("bad")).thenReturn(false);

    assertThrows(NotFoundException.class, () -> stockMovementService.calculateCurrentStock("bad"));
    verify(stockMovementRepository, never()).calculateStockByFormatId(any());
  }

  @Test
  void resyncStock_existingFormat_updatesStockAndSaves() {
    when(bookFormatRepository.findById("f-1")).thenReturn(Optional.of(format));
    when(stockMovementRepository.calculateStockByFormatId("f-1")).thenReturn(42);

    stockMovementService.resyncStock("f-1");

    ArgumentCaptor<BookFormat> captor = ArgumentCaptor.forClass(BookFormat.class);
    verify(bookFormatRepository).save(captor.capture());
    assertEquals(42, captor.getValue().getStock());
  }

  @Test
  void resyncStock_unknownFormat_throwsNotFoundException() {
    when(bookFormatRepository.findById("bad")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> stockMovementService.resyncStock("bad"));
    verify(bookFormatRepository, never()).save(any());
  }

  @Test
  void record_createsAndSavesMovement() {
    when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(movement);

    StockMovement result =
        stockMovementService.record(format, StockMovement.MovementType.SALE, -2, "sale-1");

    assertNotNull(result);
    ArgumentCaptor<StockMovement> captor = ArgumentCaptor.forClass(StockMovement.class);
    verify(stockMovementRepository).save(captor.capture());
    StockMovement saved = captor.getValue();
    assertEquals(format, saved.getBookFormat());
    assertEquals(StockMovement.MovementType.SALE, saved.getType());
    assertEquals(-2, saved.getQuantity());
    assertEquals("sale-1", saved.getReferenceId());
  }
}
