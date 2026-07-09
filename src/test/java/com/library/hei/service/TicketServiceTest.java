package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.library.hei.mail.Email;
import com.library.hei.mail.Mailer;
import com.library.hei.model.entity.Customer;
import com.library.hei.model.entity.Sale;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.pdf.TicketPdfGenerator;
import com.library.hei.repository.SaleRepository;
import com.library.hei.storage.TicketStorage;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

  @Mock private SaleRepository saleRepository;
  @Mock private TicketPdfGenerator ticketPdfGenerator;
  @Mock private TicketStorage ticketStorage;
  @Mock private Mailer mailer;

  @InjectMocks private TicketService ticketService;

  private Customer customer;
  private Sale doneSale;

  @BeforeEach
  void setUp() {
    customer =
        Customer.builder()
            .id("cust-1")
            .firstName("Jean")
            .lastName("Dupont")
            .email("jean.dupont@example.com")
            .build();

    doneSale =
        Sale.builder()
            .id("sale-1")
            .customer(customer)
            .status(Sale.SaleStatus.DONE)
            .totalAmount(new BigDecimal("25.00"))
            .saleDetails(java.util.List.of())
            .build();
  }

  @Test
  void sendTicket_withDoneSaleAndCustomerEmail_generatesUploadsAndSendsEmail() {
    byte[] pdfBytes = "%PDF-fake".getBytes();

    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(doneSale));
    when(ticketPdfGenerator.apply(doneSale)).thenReturn(pdfBytes);
    when(ticketStorage.upload(eq("tickets/sale-1.pdf"), eq(pdfBytes)))
        .thenReturn("https://bucket.s3.amazonaws.com/tickets/sale-1.pdf?signed=1");

    String result = ticketService.sendTicket("sale-1");

    assertEquals("https://bucket.s3.amazonaws.com/tickets/sale-1.pdf?signed=1", result);

    verify(ticketPdfGenerator).apply(doneSale);
    verify(ticketStorage).upload("tickets/sale-1.pdf", pdfBytes);

    var emailCaptor = org.mockito.ArgumentCaptor.forClass(Email.class);
    verify(mailer).accept(emailCaptor.capture());

    Email sentEmail = emailCaptor.getValue();
    assertEquals("jean.dupont@example.com", sentEmail.to().getAddress());
    assertTrue(sentEmail.subject().contains("sale-1"));
    assertTrue(sentEmail.htmlBody().contains("https://bucket.s3.amazonaws.com"));
    assertEquals(1, sentEmail.attachments().size());
    assertTrue(sentEmail.attachments().get(0).getName().endsWith(".pdf"));
  }

  @Test
  void sendTicket_withNonExistingSale_throwsNotFoundException() {
    when(saleRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> ticketService.sendTicket("unknown"));

    verifyNoInteractions(ticketPdfGenerator, ticketStorage, mailer);
  }

  @Test
  void sendTicket_withPendingSale_throwsBadRequestException() {
    doneSale.setStatus(Sale.SaleStatus.PENDING);
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(doneSale));

    assertThrows(BadRequestException.class, () -> ticketService.sendTicket("sale-1"));

    verifyNoInteractions(ticketPdfGenerator, ticketStorage, mailer);
  }

  @Test
  void sendTicket_withCancelledSale_throwsBadRequestException() {
    doneSale.setStatus(Sale.SaleStatus.CANCELLED);
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(doneSale));

    assertThrows(BadRequestException.class, () -> ticketService.sendTicket("sale-1"));

    verifyNoInteractions(ticketPdfGenerator, ticketStorage, mailer);
  }

  @Test
  void sendTicket_withoutCustomer_throwsBadRequestException() {
    doneSale.setCustomer(null);
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(doneSale));

    assertThrows(BadRequestException.class, () -> ticketService.sendTicket("sale-1"));

    verifyNoInteractions(ticketPdfGenerator, ticketStorage, mailer);
  }

  @Test
  void sendTicket_withCustomerWithoutEmail_throwsBadRequestException() {
    customer.setEmail(null);
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(doneSale));

    assertThrows(BadRequestException.class, () -> ticketService.sendTicket("sale-1"));

    verifyNoInteractions(ticketPdfGenerator, ticketStorage, mailer);
  }

  @Test
  void sendTicket_withCustomerBlankEmail_throwsBadRequestException() {
    customer.setEmail("   ");
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(doneSale));

    assertThrows(BadRequestException.class, () -> ticketService.sendTicket("sale-1"));

    verifyNoInteractions(ticketPdfGenerator, ticketStorage, mailer);
  }

  @Test
  void sendTicket_withInvalidCustomerEmail_throwsBadRequestException() {
    customer.setEmail("not-an-email-@@@");
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(doneSale));
    when(ticketPdfGenerator.apply(doneSale)).thenReturn(new byte[] {1, 2, 3});
    when(ticketStorage.upload(anyString(), any())).thenReturn("https://example.com/ticket.pdf");

    assertThrows(BadRequestException.class, () -> ticketService.sendTicket("sale-1"));

    verifyNoInteractions(mailer);
  }
}
