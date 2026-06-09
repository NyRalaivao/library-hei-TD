package com.library.hei.service;

import com.library.hei.entity.Book;
import com.library.hei.entity.BookFormat;
import com.library.hei.repository.BookRepository;
import com.library.hei.repository.BookFormatRepository;
import com.library.hei.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookFormatRepository bookFormatRepository;

    // CRUD Operations

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book updateBook(String id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.setTitle(bookDetails.getTitle());
        book.setIsbn(bookDetails.getIsbn());
        book.setPrice(bookDetails.getPrice());
        book.setGenres(bookDetails.getGenres());
        book.setAuthors(bookDetails.getAuthors());

        return bookRepository.save(book);
    }

    public void deleteBook(String id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    // Search Operations with multiple criteria

    public List<Book> searchBooks(String title, LocalDateTime createdAfter,
                                  LocalDateTime createdBefore, String genreId,
                                  String authorId, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Book> spec = Specification
                .where(BookSpecification.titleContains(title))
                .and(BookSpecification.createdAfter(createdAfter))
                .and(BookSpecification.createdBefore(createdBefore))
                .and(BookSpecification.hasGenre(genreId))
                .and(BookSpecification.hasAuthor(authorId))
                .and(BookSpecification.priceBetween(minPrice, maxPrice));

        return bookRepository.findAll(spec);
    }

    public List<Book> getBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> getBooksByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> getBooksCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return bookRepository.findBooksCreatedBetween(startDate, endDate);
    }

    public List<Book> getBooksCreatedBefore(LocalDateTime date) {
        return bookRepository.findBooksCreatedBefore(date);
    }

    public List<Book> getBooksCreatedAfter(LocalDateTime date) {
        return bookRepository.findBooksCreatedAfter(date);
    }

    public List<Book> getBooksByGenre(String genreId) {
        return bookRepository.findBooksByGenreId(genreId);
    }

    public List<Book> getBooksByAuthor(String authorId) {
        return bookRepository.findBooksByAuthorId(authorId);
    }

    // Book Format Operations

    public BookFormat addBookFormat(BookFormat format) {
        if (format.getBook() == null || format.getBook().getIdBook() == null) {
            throw new RuntimeException("Book must be specified for format");
        }
        return bookFormatRepository.save(format);
    }

    public List<BookFormat> getBookFormats(String bookId) {
        return bookFormatRepository.findByBook_IdBook(bookId);
    }

    public BookFormat updateBookFormatStock(String formatId, Integer newStock) {
        BookFormat format = bookFormatRepository.findById(formatId)
                .orElseThrow(() -> new RuntimeException("Book format not found with id: " + formatId));
        format.setStock(newStock);
        return bookFormatRepository.save(format);
    }

    public Optional<BookFormat> getBookFormatById(String formatId) {
        return bookFormatRepository.findById(formatId);
    }

    public void deleteBookFormat(String formatId) {
        bookFormatRepository.deleteById(formatId);
    }
}