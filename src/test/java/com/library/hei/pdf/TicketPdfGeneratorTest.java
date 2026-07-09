package com.library.hei.pdf;

import static org.junit.jupiter.api.Assertions.*;

import com.library.hei.model.entity.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class TicketPdfGeneratorTest {

  private final TicketPdfGenerator ticketPdfGenerator = new TicketPdfGenerator();

  @Test
  void apply_withFullSale_generatesValidNonEmptyPdf() {
    Customer customer =
        Customer.builder()
            .id("cust-1")
            .firstName("Jean")
            .lastName("Dupont")
            .email("jean.dupont@example.com")
            .build();
    User seller =
        User.builder().id("user-1").username("vendeur").role(User.UserRole.SELLER).build();
    Book book = Book.builder().id("book-1").title("Les Misérables").price(BigDecimal.TEN).build();
    BookFormat format =
        BookFormat.builder()
            .id("format-1")
            .book(book)
            .coverType(BookFormat.CoverType.MEDIUM)
            .price(new BigDecimal("12.50"))
            .stock(10)
            .build();

    SaleDetail detail =
        SaleDetail.builder()
            .id("detail-1")
            .bookFormat(format)
            .quantity(2)
            .unitPrice(new BigDecimal("12.50"))
            .totalPrice(new BigDecimal("25.00"))
            .build();

    Sale sale =
        Sale.builder()
            .id("sale-1")
            .customer(customer)
            .seller(seller)
            .saleDate(LocalDateTime.of(2026, 7, 9, 10, 30))
            .totalAmount(new BigDecimal("25.00"))
            .status(Sale.SaleStatus.DONE)
            .saleDetails(List.of(detail))
            .build();

    byte[] pdfBytes = ticketPdfGenerator.apply(sale);

    assertNotNull(pdfBytes);
    assertTrue(pdfBytes.length > 0, "Le PDF généré ne doit pas être vide");
    assertPdfHeader(pdfBytes);
  }

  @Test
  void apply_withSaleWithoutDetails_stillGeneratesValidPdf() {
    Sale emptySale =
        Sale.builder()
            .id("sale-2")
            .saleDate(LocalDateTime.now())
            .totalAmount(BigDecimal.ZERO)
            .status(Sale.SaleStatus.DONE)
            .saleDetails(List.of())
            .build();

    byte[] pdfBytes = ticketPdfGenerator.apply(emptySale);

    // Même avec une vente minimaliste (sans client/vendeur/détails), le PDF généré
    // doit rester un document valide : un contenu réduit n'est pas un problème.
    assertNotNull(pdfBytes);
    assertTrue(pdfBytes.length > 0);
    assertPdfHeader(pdfBytes);
  }

  private void assertPdfHeader(byte[] pdfBytes) {
    String header = new String(pdfBytes, 0, Math.min(5, pdfBytes.length));
    assertEquals("%PDF-", header, "Le fichier généré doit être un PDF valide");
  }
}
