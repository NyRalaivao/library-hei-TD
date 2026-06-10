package com.library.hei.endpoint.rest.controller.dashboard;

import com.library.hei.model.entity.Sale;
import com.library.hei.service.DashboardService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/dashboard")
public class DashboardController {
  private final DashboardService dashboardService;

  @GetMapping("/revenue/current-month")
  public Map<String, Object> getCurrentMonthRevenue() {
    BigDecimal revenue = dashboardService.getCurrentMonthRevenue();
    return Map.of("totalRevenue", revenue);
  }

  @GetMapping("/sales/pending")
  public List<Sale> getPendingSales() {
    return dashboardService.getPendingSales();
  }

  @GetMapping("/sales/recent")
  public List<Sale> getRecentSales(@RequestParam(defaultValue = "10") int limit) {
    return dashboardService.getRecentSales(limit);
  }

  @GetMapping("/books/low-stock")
  public List<Map<String, Object>> getLowStockBooks(
      @RequestParam(defaultValue = "3") int threshold) {
    return dashboardService.getLowStockBooks(threshold);
  }

  @GetMapping("/books/best-sellers")
  public List<Map<String, Object>> getBestSellers(
      @RequestParam(defaultValue = "10") int limit) {
    return dashboardService.getBestSellers(limit);
  }

  @GetMapping("/genres/revenue")
  public List<Map<String, Object>> getRevenueByGenre() {
    return dashboardService.getRevenueByGenre();
  }
}
