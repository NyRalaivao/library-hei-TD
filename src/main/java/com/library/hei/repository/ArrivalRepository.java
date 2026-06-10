package com.library.hei.repository;

import com.library.hei.model.entity.Arrival;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArrivalRepository extends JpaRepository<Arrival, String> {
  List<Arrival> findByBookIdOrderByArrivalDateDesc(String bookId);
}
