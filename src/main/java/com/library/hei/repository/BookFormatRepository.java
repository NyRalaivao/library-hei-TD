package com.library.hei.repository;

import com.library.hei.model.entity.BookFormat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookFormatRepository extends JpaRepository<BookFormat, String> {
    List<BookFormat> findByBookId(String bookId);

    List<BookFormat> findByStockLessThanEqual(int threshold);

    @Query("SELECT f FROM BookFormat f JOIN FETCH f.book b ORDER BY b.title ASC, f.coverType ASC")
    List<BookFormat> findAllWithBookOrderByBookTitle();
}