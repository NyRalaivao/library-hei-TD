package com.library.hei.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.library.hei.model.entity.Payment;
import com.library.hei.model.entity.Sale;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.PaymentRepository;
import com.library.hei.repository.SaleRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock private PaymentRepository paymentRepository;
  @Mock private SaleRepository saleRepository;

  @InjectMocks private PaymentService paymentService;

  private Sale sale;
  private Payment payment;

  @BeforeEach
  void setUp() {
    sale =
        Sale.builder()
            .id("sale-1")
            .status(Sale.SaleStatus.DONE)
            .totalAmount(new BigDecimal("50000"))
            .build();

    payment = Payment.builder().id("payment-1").method(Payment.PaymentMethod.CASH).build();
  }

  @Test
  void getAll_returnsAllPayments() {
    when(paymentRepository.findAll()).thenReturn(List.of(payment));

    List<Payment> result = paymentService.getAll();

    assertEquals(1, result.size());
    verify(paymentRepository).findAll();
  }

  @Test
  void getById_found() {
    when(paymentRepository.findById("payment-1")).thenReturn(Optional.of(payment));

    Payment result = paymentService.getById("payment-1");

    assertNotNull(result);
    assertEquals("payment-1", result.getId());
  }

  @Test
  void getById_notFound_throwsNotFoundException() {
    when(paymentRepository.findById("unknown")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> paymentService.getById("unknown"));
  }

  @Test
  void createPayment_validPayment_savesPayment() {
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(sale));
    when(paymentRepository.findBySaleId("sale-1")).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

    Payment result = paymentService.createPayment("sale-1", payment);

    assertNotNull(result);

    verify(paymentRepository).save(payment);
  }

  @Test
  void createPayment_saleNotFound_throwsNotFoundException() {
    when(saleRepository.findById("sale-1")).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> paymentService.createPayment("sale-1", payment));

    verify(paymentRepository, never()).save(any());
  }

  @Test
  void createPayment_saleStatusNotDone_throwsBadRequestException() {
    sale.setStatus(Sale.SaleStatus.PENDING);

    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(sale));

    assertThrows(BadRequestException.class, () -> paymentService.createPayment("sale-1", payment));

    verify(paymentRepository, never()).save(any());
  }

  @Test
  void createPayment_paymentAlreadyExists_throwsBadRequestException() {
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(sale));

    Payment existing = Payment.builder().id("existing").sale(sale).build();

    when(paymentRepository.findBySaleId("sale-1")).thenReturn(Optional.of(existing));

    assertThrows(BadRequestException.class, () -> paymentService.createPayment("sale-1", payment));

    verify(paymentRepository, never()).save(any());
  }

  @Test
  void createPayment_setsSaleAndAmountBeforeSave() {
    when(saleRepository.findById("sale-1")).thenReturn(Optional.of(sale));
    when(paymentRepository.findBySaleId("sale-1")).thenReturn(Optional.empty());
    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Payment result = paymentService.createPayment("sale-1", payment);

    assertEquals(sale, result.getSale());
    assertEquals(sale.getTotalAmount(), result.getAmount());
  }
}
