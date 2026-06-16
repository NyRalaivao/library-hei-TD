package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.*;
import com.library.hei.repository.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private SaleDetailRepository saleDetailRepository;
  @Mock private BookFormatRepository bookFormatRepository;

  @InjectMocks private DashboardService dashboardService;

  private Sale doneSale;
  private Sale pendingSale;
  private BookFormat lowStockFormat;
  private BookFormat okStockFormat;
  private Book book;

  @BeforeEach
  void setUp() {
    book = Book.builder().id("book-1").title("Les Misérables").price(BigDecimal.TEN).build();

    lowStockFormat =
        BookFormat.builder()
            .id("fmt-1")
            .book(book)
            .coverType(BookFormat.CoverType.POCKET)
            .price(new BigDecimal("9.90"))
            .stock(2)
            .build();

    okStockFormat =
        BookFormat.builder()
            .id("fmt-2")
            .book(book)
            .coverType(BookFormat.CoverType.MEDIUM)
            .price(new BigDecimal("12.50"))
            .stock(15)
            .build();

    Customer customer = Customer.builder().id("c-1").firstName("Marie").lastName("Curie").build();
    User seller = User.builder().id("u-1").username("seller").role(User.UserRole.SELLER).build();

    doneSale =
        Sale.builder()
            .id("sale-done")
            .customer(customer)
            .seller(seller)
            .saleDate(LocalDateTime.now())
            .status(Sale.SaleStatus.DONE)
            .totalAmount(new BigDecimal("25.00"))
            .build();

    pendingSale =
        Sale.builder()
            .id("sale-pending")
            .customer(customer)
            .seller(seller)
            .saleDate(LocalDateTime.now())
            .status(Sale.SaleStatus.PENDING)
            .totalAmount(new BigDecimal("12.50"))
            .build();
  }

  // ─── getCurrentMonthRevenue ───────────────────────────────────────────────

  @Test
  void getCurrentMonthRevenue_returnsRepositoryValue() {
    when(saleRepository.sumCurrentMonthRevenue()).thenReturn(new BigDecimal("1500.00"));

    BigDecimal result = dashboardService.getCurrentMonthRevenue();

    assertEquals(new BigDecimal("1500.00"), result);
    verify(saleRepository).sumCurrentMonthRevenue();
  }

  @Test
  void getCurrentMonthRevenue_returnsZeroWhenNull() {
    when(saleRepository.sumCurrentMonthRevenue()).thenReturn(null);

    BigDecimal result = dashboardService.getCurrentMonthRevenue();

    assertEquals(BigDecimal.ZERO, result);
  }

  // ─── getPendingSales ──────────────────────────────────────────────────────

  @Test
  void getPendingSales_returnsOnlyPending() {
    when(saleRepository.findByStatus(Sale.SaleStatus.PENDING)).thenReturn(List.of(pendingSale));

    List<Sale> result = dashboardService.getPendingSales();

    assertEquals(1, result.size());
    assertEquals(Sale.SaleStatus.PENDING, result.get(0).getStatus());
  }

  @Test
  void getPendingSales_emptyWhenNoPending() {
    when(saleRepository.findByStatus(Sale.SaleStatus.PENDING)).thenReturn(List.of());
    assertTrue(dashboardService.getPendingSales().isEmpty());
  }

  // ─── getRecentSales ───────────────────────────────────────────────────────

  @Test
  void getRecentSales_returnsOnlyDone() {
    when(saleRepository.findByStatusOrderBySaleDateDesc(
            eq(Sale.SaleStatus.DONE), any(PageRequest.class)))
        .thenReturn(List.of(doneSale));

    List<Sale> result = dashboardService.getRecentSales(10);

    assertEquals(1, result.size());
    assertEquals(Sale.SaleStatus.DONE, result.get(0).getStatus());
  }

  // ─── getLowStockBooks ─────────────────────────────────────────────────────

  @Test
  void getLowStockBooks_returnsOnlyBelowThreshold() {
    when(bookFormatRepository.findByStockLessThanEqual(3)).thenReturn(List.of(lowStockFormat));

    List<Map<String, Object>> result = dashboardService.getLowStockBooks(3);

    assertEquals(1, result.size());
    assertEquals("Les Misérables", result.get(0).get("title"));
    assertEquals(2, result.get(0).get("stock"));
  }

  @Test
  void getLowStockBooks_emptyWhenAllStockOk() {
    when(bookFormatRepository.findByStockLessThanEqual(3)).thenReturn(List.of());
    assertTrue(dashboardService.getLowStockBooks(3).isEmpty());
  }

  @Test
  void getLowStockBooks_doesNotIncludeOkStock() {
    when(bookFormatRepository.findByStockLessThanEqual(3)).thenReturn(List.of());

    List<Map<String, Object>> result = dashboardService.getLowStockBooks(3);

    assertTrue(result.stream().noneMatch(m -> (int) m.get("stock") > 3));
  }

  // ─── getBestSellers ───────────────────────────────────────────────────────

  @Test
  void getBestSellers_returnsRankedList() {
    Object[] row = new Object[] {book, 42L};
    when(saleDetailRepository.findBestSellers(any(PageRequest.class))).thenReturn(List.of(row));

    List<Map<String, Object>> result = dashboardService.getBestSellers(5);

    assertEquals(1, result.size());
    assertEquals("Les Misérables", result.get(0).get("title"));
    assertEquals(42L, result.get(0).get("totalSold"));
  }

  @Test
  void getBestSellers_emptyWhenNoSales() {
    when(saleDetailRepository.findBestSellers(any(PageRequest.class))).thenReturn(List.of());
    assertTrue(dashboardService.getBestSellers(5).isEmpty());
  }

  // ─── getRevenueByGenre ────────────────────────────────────────────────────

  @Test
  void getRevenueByGenre_returnsGenreRevenuePairs() {
    Object[] row = new Object[] {"Roman", new BigDecimal("850.00")};
    when(saleDetailRepository.findRevenueByGenre()).thenReturn(List.of(row));

    List<Map<String, Object>> result = dashboardService.getRevenueByGenre();

    assertEquals(1, result.size());
    assertEquals("Roman", result.get(0).get("genre"));
    assertEquals(new BigDecimal("850.00"), result.get(0).get("revenue"));
  }

  @Test
  void getRevenueByGenre_emptyWhenNoSales() {
    when(saleDetailRepository.findRevenueByGenre()).thenReturn(List.of());
    assertTrue(dashboardService.getRevenueByGenre().isEmpty());
  }
}
