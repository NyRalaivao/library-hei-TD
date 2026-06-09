package com.library.hei.repository;

import com.library.hei.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.createdAt >= :startDate AND b.createdAt <= :endDate")
    List<Book> findBooksCreatedBetween(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Book b WHERE b.createdAt <= :date")
    List<Book> findBooksCreatedBefore(@Param("date") LocalDateTime date);

    @Query("SELECT b FROM Book b WHERE b.createdAt >= :date")
    List<Book> findBooksCreatedAfter(@Param("date") LocalDateTime date);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.idGenre = :genreId")
    List<Book> findBooksByGenreId(@Param("genreId") String genreId);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.authors a WHERE a.idAuthor = :authorId")
    List<Book> findBooksByAuthorId(@Param("authorId") String authorId);
}