package com.library.hei.repository;

import com.library.hei.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
  Optional<Payment> findBySaleId(String saleId);
}
