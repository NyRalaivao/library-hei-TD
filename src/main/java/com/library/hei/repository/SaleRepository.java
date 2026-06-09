package com.library.hei.repository;

import com.library.hei.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String> {
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Sale> findByCustomer_IdCustomer(String customerId);
}