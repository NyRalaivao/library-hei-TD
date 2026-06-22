package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.*;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.InsufficientStockException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private BookFormatRepository bookFormatRepository;
  @Mock private CustomerRepository customerRepository;
  @Mock private UserRepository userRepository;
  // NOUVEAU : mock de StockMovementService
  @Mock private StockMovementService stockMovementService;

  @InjectMocks private SaleService saleService;

  private Customer customer;
  private User seller;
  private Book book;
  private BookFormat format;
  private Sale pendingSale;

  @BeforeEach
  void setUp() {
    customer = Customer.builder().id("cust-1").firstName("Jean").lastName("Dupont").build();
    seller = User.builder().id("user-1").username("vendeur").role(User.UserRole.SELLER).build();

    book =
        Book.builder()
            .id("book-1")
            .title("Les Misérables")
            .isbn("978-1")
            .price(BigDecimal.TEN)
            .build();

    format =
        BookFormat.builder()
            .id("format-1")
            .book(book)
            .coverType(BookFormat.CoverType.MEDIUM)
            .price(new BigDecimal("12.50"))
            .stock(10)
            .build();

    List<SaleDetail> details = new ArrayList<>();
    SaleDetail detail =
        SaleDetail.builder()
            .id("detail-1")
            .bookFormat(format)
            .quantity(2)
            .unitPrice(format.getPrice())
            .totalPrice(format.getPrice().multiply(BigDecimal.valueOf(2)))
            .build();
    details.add(detail);

    pendingSale =
        Sale.builder()
            .id("sale-1")
            .customer(customer)
            .seller(seller)
            .saleDate(LocalDateTime.now())
            .status(Sale.SaleStatus.PENDING)
            .totalAmount(new BigDecimal("25.00"))
            .saleDetails(details)
            .build();
    detail.setSale(pendingSale);
  }

  // ─── getPendingSales ───────────────────────────────────────────────────────

  @Test
  void getPendingSales_returnsOnlyPending() {
    when(saleRepository.findByStatus(Sale.SaleStatus.PENDING)).thenReturn(List.of(pendingSale));

    List<Sale> result = saleService.getPendingSales();

    assertEquals(1, result.size());
    assertEquals(Sale.SaleStatus.PENDING, result.get(0).getStatus());
  }

  @Test
  void getPendingSales_returnsEmptyWhenNone() {
    when(saleRepository.findByStatus(Sale.SaleStatus.PENDING)).thenReturn(List.of());
    assertTrue(saleService.getPendingSales().isEmpty());
  }

  // ─── getById ──────────────────────────────────────────────────────────────

  @Test
  void getById_found() {
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(pendingSale));
    assertEquals("sale-1", saleService.getById("sale-1").getId());
  }

  @Test
  void getById_notFound_throwsNotFoundException() {
    when(saleRepository.findById("unknown")).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> saleService.getById("unknown"));
  }

  // ─── createSale ───────────────────────────────────────────────────────────

  @Test
  void createSale_ok_calculatesTotalAmount() {
    when(customerRepository.findById("cust-1")).thenReturn(Optional.of(customer));
    when(userRepository.findById("user-1")).thenReturn(Optional.of(seller));
    when(bookFormatRepository.findById("format-1")).thenReturn(Optional.of(format));
    when(saleRepository.save(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

    Sale result =
        saleService.createSale(
            "cust-1", "user-1", List.of(new SaleService.SaleItemRequest("format-1", 3)));

    assertEquals(Sale.SaleStatus.PENDING, result.getStatus());
    assertEquals(new BigDecimal("37.50"), result.getTotalAmount()); // 3 × 12.50

    // createSale ne doit PAS enregistrer de mouvement (vente encore PENDING)
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }

  @Test
  void createSale_customerNotFound_throwsNotFoundException() {
    when(customerRepository.findById("bad")).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> saleService.createSale("bad", "user-1", List.of()));
  }

  @Test
  void createSale_sellerNotFound_throwsNotFoundException() {
    when(customerRepository.findById("cust-1")).thenReturn(Optional.of(customer));
    when(userRepository.findById("bad")).thenReturn(Optional.empty());
    assertThrows(NotFoundException.class, () -> saleService.createSale("cust-1", "bad", List.of()));
  }

  // ─── confirmSale ──────────────────────────────────────────────────────────

  @Test
  void confirmSale_decrementsStockAndRecordsMovement() {
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(pendingSale));
    when(bookFormatRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(saleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Sale result = saleService.confirmSale("sale-1");

    assertEquals(Sale.SaleStatus.DONE, result.getStatus());
    assertEquals(8, format.getStock()); // 10 - 2

    // NOUVEAU : vérifier que le mouvement de stock SALE est enregistré
    verify(stockMovementService)
        .record(
            eq(format),
            eq(StockMovement.MovementType.SALE),
            eq(-2), // NÉGATIF = sortie de stock
            eq("sale-1") // référence vers la vente confirmée
            );
  }

  @Test
  void confirmSale_alreadyDone_throwsBadRequest() {
    pendingSale.setStatus(Sale.SaleStatus.DONE);
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(pendingSale));

    assertThrows(BadRequestException.class, () -> saleService.confirmSale("sale-1"));
    verify(bookFormatRepository, never()).save(any());
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }

  @Test
  void confirmSale_insufficientStock_throwsException() {
    format.setStock(1); // insuffisant pour quantité = 2
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(pendingSale));

    assertThrows(InsufficientStockException.class, () -> saleService.confirmSale("sale-1"));
    verify(saleRepository, never()).save(any());
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }

  // ─── cancelSale ───────────────────────────────────────────────────────────

  @Test
  void cancelSale_pending_setsStatusCancelled_noStockMovement() {
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(pendingSale));
    when(saleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Sale result = saleService.cancelSale("sale-1");

    assertEquals(Sale.SaleStatus.CANCELLED, result.getStatus());
    // Le stock ne doit pas bouger : il n'avait pas été décrémenté (vente PENDING)
    assertEquals(10, format.getStock());
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }

  @Test
  void cancelSale_done_throwsBadRequest() {
    pendingSale.setStatus(Sale.SaleStatus.DONE);
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(pendingSale));

    assertThrows(BadRequestException.class, () -> saleService.cancelSale("sale-1"));
    verify(stockMovementService, never()).record(any(), any(), anyInt(), any());
  }
}
