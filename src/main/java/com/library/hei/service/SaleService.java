package com.library.hei.service;

import com.library.hei.model.entity.*;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.InsufficientStockException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SaleService {

  private final SaleRepository saleRepository;
  private final BookFormatRepository bookFormatRepository;
  private final CustomerRepository customerRepository;
  private final UserRepository userRepository;

  public List<Sale> getPendingSales() {
    return saleRepository.findByStatus(Sale.SaleStatus.PENDING);
  }

  public List<Sale> getRecentSales(int limit) {
    return saleRepository.findByStatusOrderBySaleDateDesc(
        Sale.SaleStatus.DONE, PageRequest.of(0, limit));
  }

  public Sale getById(String id) {
    return saleRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Vente id=" + id + " introuvable"));
  }

  public List<Sale> getAll() {
    return saleRepository.findAll();
  }

  @Transactional
  public Sale createSale(String customerId, String sellerId, List<SaleItemRequest> items) {
    Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new NotFoundException("Client id=" + customerId + " introuvable"));
    User seller = userRepository.findById(sellerId)
        .orElseThrow(() -> new NotFoundException("Vendeur id=" + sellerId + " introuvable"));

    Sale sale = Sale.builder()
        .customer(customer)
        .seller(seller)
        .saleDate(LocalDateTime.now())
        .status(Sale.SaleStatus.PENDING)
        .totalAmount(BigDecimal.ZERO)
        .saleDetails(new ArrayList<>())
        .build();

    BigDecimal total = BigDecimal.ZERO;
    for (SaleItemRequest item : items) {
      BookFormat format = bookFormatRepository.findById(item.getFormatId())
          .orElseThrow(() -> new NotFoundException("Format id=" + item.getFormatId() + " introuvable"));

      SaleDetail detail = SaleDetail.builder()
          .sale(sale)
          .bookFormat(format)
          .quantity(item.getQuantity())
          .unitPrice(format.getPrice())
          .totalPrice(format.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
          .build();

      sale.getSaleDetails().add(detail);
      total = total.add(detail.getTotalPrice());
    }
    sale.setTotalAmount(total);
    return saleRepository.save(sale);
  }

  @Transactional
  public Sale confirmSale(String saleId) {
    Sale sale = getById(saleId);

    if (sale.getStatus() != Sale.SaleStatus.PENDING) {
      throw new BadRequestException("Seules les ventes PENDING peuvent être confirmées");
    }

    // Vérifier et décrémenter le stock
    for (SaleDetail detail : sale.getSaleDetails()) {
      BookFormat format = detail.getBookFormat();
      int available = format.getStock();
      int requested = detail.getQuantity();

      if (available < requested) {
        throw new InsufficientStockException(
            format.getBook().getTitle(), requested, available);
      }
      format.setStock(available - requested);
      bookFormatRepository.save(format);
    }

    sale.setStatus(Sale.SaleStatus.DONE);
    return saleRepository.save(sale);
  }

  @Transactional
  public Sale cancelSale(String saleId) {
    Sale sale = getById(saleId);
    if (sale.getStatus() == Sale.SaleStatus.DONE) {
      throw new BadRequestException("Une vente DONE ne peut pas être annulée");
    }
    sale.setStatus(Sale.SaleStatus.CANCELLED);
    return saleRepository.save(sale);
  }

  // DTO interne pour la création
  public record SaleItemRequest(String formatId, int quantity) {
    public String getFormatId() { return formatId; }
    public int getQuantity() { return quantity; }
  }
}
