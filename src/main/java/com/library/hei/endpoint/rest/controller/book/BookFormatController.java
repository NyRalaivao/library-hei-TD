package com.library.hei.endpoint.rest.controller.book;

import com.library.hei.model.entity.BookFormat;
import com.library.hei.service.BookFormatService;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/books/{bookId}")
public class BookFormatController {

  private final BookFormatService bookFormatService;

  @GetMapping("/formats")
  public List<BookFormat> getFormats(@PathVariable String bookId) {
    return bookFormatService.getFormatsByBook(bookId);
  }

  @GetMapping("/stock")
  public Map<String, Object> getTotalStock(@PathVariable String bookId) {
    int stock = bookFormatService.getBookTotalStock(bookId);
    return Map.of("bookId", bookId, "totalStock", stock);
  }

  @GetMapping("/formats/{formatId}/stock")
  public Map<String, Object> getCopyStock(
      @PathVariable String bookId, @PathVariable String formatId) {
    int stock = bookFormatService.getBookCopyStock(bookId, formatId);
    return Map.of("bookId", bookId, "formatId", formatId, "stock", stock);
  }

  @GetMapping("/formats/{formatId}/availability")
  public Map<String, Object> checkAvailability(
      @PathVariable String formatId, @RequestParam int quantity) {
    boolean available = bookFormatService.hasEnoughStock(formatId, quantity);
    return Map.of("formatId", formatId, "requestedQuantity", quantity, "available", available);
  }
}
