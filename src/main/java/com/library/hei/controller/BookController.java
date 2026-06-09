package com.library.hei.controller;

import com.library.hei.entity.Book;
import com.library.hei.entity.BookFormat;
import com.library.hei.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // GET all books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // GET book by ID
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable String id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET books with multiple criteria
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdBefore,
            @RequestParam(required = false) String genreId,
            @RequestParam(required = false) String authorId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(bookService.searchBooks(
                title, createdAfter, createdBefore, genreId, authorId, minPrice, maxPrice));
    }

    // GET books by title (startsWith)
    @GetMapping("/title/{title}")
    public ResponseEntity<List<Book>> getBooksByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    // GET books by ISBN
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<List<Book>> getBooksByIsbn(@PathVariable String isbn) {
        return ResponseEntity.ok(bookService.getBooksByIsbn(isbn));
    }

    // GET books created after specific date
    @GetMapping("/created-after")
    public ResponseEntity<List<Book>> getBooksCreatedAfter(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(bookService.getBooksCreatedAfter(date));
    }

    // GET books created before specific date
    @GetMapping("/created-before")
    public ResponseEntity<List<Book>> getBooksCreatedBefore(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return ResponseEntity.ok(bookService.getBooksCreatedBefore(date));
    }

    // GET books created between dates
    @GetMapping("/created-between")
    public ResponseEntity<List<Book>> getBooksCreatedBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(bookService.getBooksCreatedBetween(startDate, endDate));
    }

    // GET books by genre
    @GetMapping("/genre/{genreId}")
    public ResponseEntity<List<Book>> getBooksByGenre(@PathVariable String genreId) {
        return ResponseEntity.ok(bookService.getBooksByGenre(genreId));
    }

    // GET books by author
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Book>> getBooksByAuthor(@PathVariable String authorId) {
        return ResponseEntity.ok(bookService.getBooksByAuthor(authorId));
    }

    // POST create book
    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book created = bookService.createBook(book);
        return ResponseEntity.ok(created);
    }

    // PUT update book
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable String id, @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    // DELETE book
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable String id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Book Format Endpoints ====================

    // POST add book format
    @PostMapping("/formats")
    public ResponseEntity<BookFormat> addBookFormat(@RequestBody BookFormat format) {
        return ResponseEntity.ok(bookService.addBookFormat(format));
    }

    // GET book formats by book ID
    @GetMapping("/{bookId}/formats")
    public ResponseEntity<List<BookFormat>> getBookFormats(@PathVariable String bookId) {
        return ResponseEntity.ok(bookService.getBookFormats(bookId));
    }

    // GET book format by ID
    @GetMapping("/formats/{formatId}")
    public ResponseEntity<BookFormat> getBookFormatById(@PathVariable String formatId) {
        return bookService.getBookFormatById(formatId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH update book format stock
    @PatchMapping("/formats/{formatId}/stock")
    public ResponseEntity<BookFormat> updateBookFormatStock(
            @PathVariable String formatId,
            @RequestParam Integer stock) {
        return ResponseEntity.ok(bookService.updateBookFormatStock(formatId, stock));
    }

    // DELETE book format
    @DeleteMapping("/formats/{formatId}")
    public ResponseEntity<Void> deleteBookFormat(@PathVariable String formatId) {
        bookService.deleteBookFormat(formatId);
        return ResponseEntity.noContent().build();
    }
}