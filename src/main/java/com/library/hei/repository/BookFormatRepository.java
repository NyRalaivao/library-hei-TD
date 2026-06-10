package com.library.hei.repository;

import com.library.hei.model.entity.BookFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookFormatRepository extends JpaRepository<BookFormat, String> {
  List<BookFormat> findByBookId(String bookId);
  List<BookFormat> findByStockLessThanEqual(int threshold);
}
