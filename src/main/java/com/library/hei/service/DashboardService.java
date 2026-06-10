package com.library.hei.service;

import com.library.hei.model.entity.Book;
import com.library.hei.model.entity.Sale;
import com.library.hei.repository.BookFormatRepository;
import com.library.hei.repository.SaleDetailRepository;
import com.library.hei.repository.SaleRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@AllArgsConstructor
public class DashboardService {

  private final SaleRepository saleRepository;
  private final SaleDetailRepository saleDetailRepository;
  private final BookFormatRepository bookFormatRepository;

  public BigDecimal getCurrentMonthRevenue() {
    BigDecimal revenue = saleRepository.sumCurrentMonthRevenue();
    return revenue != null ? revenue : BigDecimal.ZERO;
  }

  public List<Sale> getPendingSales() {
    return saleRepository.findByStatus(Sale.SaleStatus.PENDING);
  }

  public List<Sale> getRecentSales(int limit) {
    return saleRepository.findByStatusOrderBySaleDateDesc(
        Sale.SaleStatus.DONE, PageRequest.of(0, limit));
  }

  public List<Map<String, Object>> getLowStockBooks(int threshold) {
    return bookFormatRepository.findByStockLessThanEqual(threshold).stream()
        .map(f -> {
          Map<String, Object> m = new LinkedHashMap<>();
          m.put("bookId", f.getBook().getId());
          m.put("title", f.getBook().getTitle());
          m.put("coverType", f.getCoverType());
          m.put("stock", f.getStock());
          return m;
        }).toList();
  }

  public List<Map<String, Object>> getBestSellers(int limit) {
    return saleDetailRepository.findBestSellers(PageRequest.of(0, limit)).stream()
        .map(row -> {
          Book book = (Book) row[0];
          Long totalSold = ((Number) row[1]).longValue();
          Map<String, Object> m = new LinkedHashMap<>();
          m.put("bookId", book.getId());
          m.put("title", book.getTitle());
          m.put("totalSold", totalSold);
          return m;
        }).toList();
  }

  public List<Map<String, Object>> getRevenueByGenre() {
    return saleDetailRepository.findRevenueByGenre().stream()
        .map(row -> {
          Map<String, Object> m = new LinkedHashMap<>();
          m.put("genre", row[0]);
          m.put("revenue", row[1]);
          return m;
        }).toList();
  }
}
