package com.library.hei.repository;

import com.library.hei.model.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

  @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT(:prefix, '%'))")
  List<Book> findByTitleStartingWithIgnoreCase(@Param("prefix") String prefix, Pageable pageable);

  @Query("SELECT b FROM Book b WHERE b.creationDatetime < :before")
  List<Book> findByCreationDatetimeBefore(@Param("before") Instant before, Pageable pageable);

  @Query("SELECT b FROM Book b WHERE b.creationDatetime > :after")
  List<Book> findByCreationDatetimeAfter(@Param("after") Instant after, Pageable pageable);

  @Query("SELECT b FROM Book b WHERE "
      + "(:titlePrefix IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT(:titlePrefix, '%'))) "
      + "AND (:createdBefore IS NULL OR b.creationDatetime < :createdBefore) "
      + "AND (:createdAfter IS NULL OR b.creationDatetime > :createdAfter)")
  List<Book> findByFilters(
      @Param("titlePrefix") String titlePrefix,
      @Param("createdBefore") Instant createdBefore,
      @Param("createdAfter") Instant createdAfter,
      Pageable pageable);
}
