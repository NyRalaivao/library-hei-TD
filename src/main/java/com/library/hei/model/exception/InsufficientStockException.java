package com.library.hei.model.exception;

public class InsufficientStockException extends RuntimeException {
  public InsufficientStockException(String bookTitle, int requested, int available) {
    super(
        "Stock insuffisant pour '"
            + bookTitle
            + "' : demandé="
            + requested
            + ", disponible="
            + available);
  }
}
