package com.library.hei.repository;

import com.library.hei.model.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

  Optional<Book> findByIsbn(String isbn);

  @Query("SELECT b FROM Book b JOIN b.formats f WHERE f.stock <= :threshold")
  List<Book> findLowStockBooks(@Param("threshold") int threshold, Pageable pageable);

  @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT(:prefix, '%'))")
  List<Book> findByTitleStartingWith(@Param("prefix") String prefix, Pageable pageable);
}
