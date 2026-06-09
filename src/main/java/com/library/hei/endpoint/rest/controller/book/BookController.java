package com.library.hei.endpoint.rest.controller.book;

import com.library.hei.model.entity.Book;
import com.library.hei.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@AllArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping("/books")
  public List<Book> getBooks(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String titlePrefix,
      @RequestParam(required = false) Instant createdBefore,
      @RequestParam(required = false) Instant createdAfter) {
    return bookService.getBooks(page, pageSize, titlePrefix, createdBefore, createdAfter);
  }

  @GetMapping("/books/{id}")
  public Book getBookById(@PathVariable String id) {
    return bookService.getBookById(id);
  }

  @PutMapping("/books/{id}")
  public Book crupdateBook(@PathVariable String id, @RequestBody Book book) {
    return bookService.crupdateBook(id, book);
  }

  @PostMapping("/books/{id}/copy")
  public Book importBook(@PathVariable String id) {
    return bookService.importBook(id);
  }

  @DeleteMapping("/books/{id}")
  public Book deleteBook(@PathVariable String id) {
    return bookService.deleteBook(id);
  }
}
