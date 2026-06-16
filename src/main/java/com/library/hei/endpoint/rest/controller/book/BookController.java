package com.library.hei.endpoint.rest.controller.book;

import com.library.hei.model.entity.Book;
import com.library.hei.service.BookService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/books")
public class BookController {
  private final BookService bookService;

  @GetMapping
  public List<Book> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int pageSize,
      @RequestParam(required = false) String titlePrefix) {
    if (titlePrefix != null) return bookService.searchByTitle(titlePrefix, page, pageSize);
    return bookService.getAll(page, pageSize);
  }

  @GetMapping("/{id}")
  public Book getById(@PathVariable String id) {
    return bookService.getById(id);
  }

  @GetMapping("/isbn/{isbn}")
  public Book getByIsbn(@PathVariable String isbn) {
    return bookService.getByIsbn(isbn);
  }

  @PutMapping("/{id}")
  public Book crupdate(@PathVariable String id, @RequestBody Book book) {
    return bookService.crupdate(id, book);
  }

  @DeleteMapping("/{id}")
  public Book delete(@PathVariable String id) {
    return bookService.delete(id);
  }
}
