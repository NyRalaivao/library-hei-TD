package com.library.hei.service;

import com.library.hei.model.entity.Payment;
import com.library.hei.model.entity.Sale;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.PaymentRepository;
import com.library.hei.repository.SaleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService {
  private final PaymentRepository paymentRepository;
  private final SaleRepository saleRepository;

  public List<Payment> getAll() { return paymentRepository.findAll(); }

  public Payment getById(String id) {
    return paymentRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Paiement id=" + id + " introuvable"));
  }

  @Transactional
  public Payment createPayment(String saleId, Payment payment) {
    Sale sale = saleRepository.findById(saleId)
        .orElseThrow(() -> new NotFoundException("Vente id=" + saleId + " introuvable"));

    if (sale.getStatus() != Sale.SaleStatus.DONE)
      throw new BadRequestException("Le paiement ne peut être enregistré que sur une vente DONE");

    if (paymentRepository.findBySaleId(saleId).isPresent())
      throw new BadRequestException("Cette vente a déjà un paiement enregistré");

    payment.setSale(sale);
    payment.setAmount(sale.getTotalAmount());
    return paymentRepository.save(payment);
  }
}
