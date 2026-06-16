package com.library.hei.repository;

import com.library.hei.model.entity.SaleDetail;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, String> {

  @Query(
      "SELECT sd.bookFormat.book, SUM(sd.quantity) as totalSold "
          + "FROM SaleDetail sd "
          + "JOIN sd.sale s "
          + "WHERE s.status = 'DONE' "
          + "GROUP BY sd.bookFormat.book "
          + "ORDER BY totalSold DESC")
  List<Object[]> findBestSellers(Pageable pageable);

  @Query(
      "SELECT g.name, SUM(sd.quantity * sd.unitPrice) "
          + "FROM SaleDetail sd "
          + "JOIN sd.bookFormat.book b "
          + "JOIN b.genres g "
          + "JOIN sd.sale s "
          + "WHERE s.status = 'DONE' "
          + "GROUP BY g.name "
          + "ORDER BY SUM(sd.quantity * sd.unitPrice) DESC")
  List<Object[]> findRevenueByGenre();
}
