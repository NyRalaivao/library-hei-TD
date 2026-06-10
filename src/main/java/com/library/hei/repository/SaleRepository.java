package com.library.hei.repository;

import com.library.hei.model.entity.Sale;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, String> {

  List<Sale> findByStatus(Sale.SaleStatus status);

  List<Sale> findByStatusOrderBySaleDateDesc(Sale.SaleStatus status, Pageable pageable);

  @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s " +
      "WHERE s.status = 'DONE' " +
      "AND MONTH(s.saleDate) = MONTH(CURRENT_DATE) " +
      "AND YEAR(s.saleDate) = YEAR(CURRENT_DATE)")
  BigDecimal sumCurrentMonthRevenue();
}
