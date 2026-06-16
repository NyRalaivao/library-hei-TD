package com.library.hei.repository;

import com.library.hei.model.entity.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
  Optional<Payment> findBySaleId(String saleId);
}
