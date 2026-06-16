package com.library.hei.endpoint.rest.controller.payment;

import com.library.hei.model.entity.Payment;
import com.library.hei.service.PaymentService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
  private final PaymentService paymentService;

  @GetMapping
  public List<Payment> getAll() {
    return paymentService.getAll();
  }

  @GetMapping("/{id}")
  public Payment getById(@PathVariable String id) {
    return paymentService.getById(id);
  }

  @PostMapping("/sale/{saleId}")
  @ResponseStatus(HttpStatus.CREATED)
  public Payment createForSale(@PathVariable String saleId, @RequestBody Payment payment) {
    return paymentService.createPayment(saleId, payment);
  }
}
