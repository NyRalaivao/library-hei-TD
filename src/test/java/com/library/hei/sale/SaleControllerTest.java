package com.library.hei.sale;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.library.hei.endpoint.rest.controller.GlobalExceptionHandler;
import com.library.hei.endpoint.rest.controller.sale.SaleController;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.service.SaleService;
import com.library.hei.service.TicketService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleController.class)
@Import(GlobalExceptionHandler.class)
class SaleControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SaleService saleService;

  @MockBean private TicketService ticketService;

  @Test
  void sendTicket_withDoneSale_returns200AndTicketUrl() throws Exception {
    when(ticketService.sendTicket("sale-1"))
        .thenReturn("https://bucket.s3.amazonaws.com/tickets/sale-1.pdf?signed=1");

    mockMvc
        .perform(post("/sales/sale-1/ticket"))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.ticketUrl")
                .value("https://bucket.s3.amazonaws.com/tickets/sale-1.pdf?signed=1"));

    verify(ticketService).sendTicket("sale-1");
  }

  @Test
  void sendTicket_withNonExistingSale_returns404() throws Exception {
    when(ticketService.sendTicket("unknown"))
        .thenThrow(new NotFoundException("Vente id=unknown introuvable"));

    mockMvc
        .perform(post("/sales/unknown/ticket"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("NOT_FOUND"));
  }

  @Test
  void sendTicket_withPendingSale_returns400() throws Exception {
    when(ticketService.sendTicket("sale-1"))
        .thenThrow(
            new BadRequestException(
                "Le ticket ne peut être envoyé que pour une vente confirmée (DONE)"));

    mockMvc
        .perform(post("/sales/sale-1/ticket"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
  }

  @Test
  void sendTicket_withCustomerWithoutEmail_returns400() throws Exception {
    when(ticketService.sendTicket("sale-1"))
        .thenThrow(new BadRequestException("Le client de cette vente n'a pas d'adresse email"));

    mockMvc.perform(post("/sales/sale-1/ticket")).andExpect(status().isBadRequest());
  }
}
