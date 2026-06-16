package com.library.hei.repository;

import com.library.hei.model.entity.Arrival;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArrivalRepository extends JpaRepository<Arrival, String> {
  List<Arrival> findByBookIdOrderByArrivalDateDesc(String bookId);
}
