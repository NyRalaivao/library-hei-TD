package com.library.hei.endpoint.rest.controller.sale;

import com.library.hei.model.entity.Sale;
import com.library.hei.service.SaleService;
import com.library.hei.service.SaleService.SaleItemRequest;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/sales")
public class SaleController {
  private final SaleService saleService;

  @GetMapping
  public List<Sale> getAll() {
    return saleService.getAll();
  }

  @GetMapping("/{id}")
  public Sale getById(@PathVariable String id) {
    return saleService.getById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Sale create(@RequestBody CreateSaleRequest request) {
    return saleService.createSale(request.customerId(), request.sellerId(), request.items());
  }

  @PatchMapping("/{id}/confirm")
  public Sale confirm(@PathVariable String id) {
    return saleService.confirmSale(id);
  }

  @PatchMapping("/{id}/cancel")
  public Sale cancel(@PathVariable String id) {
    return saleService.cancelSale(id);
  }

  public record CreateSaleRequest(
      String customerId, String sellerId, List<SaleItemRequest> items) {}
}
