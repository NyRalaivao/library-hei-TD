package com.library.hei.endpoint.rest.controller;

import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.InsufficientStockException;
import com.library.hei.model.exception.NotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleNotFound(NotFoundException e) {
    return Map.of("error", e.getMessage(), "type", "NOT_FOUND");
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleBadRequest(BadRequestException e) {
    return Map.of("error", e.getMessage(), "type", "BAD_REQUEST");
  }

  @ExceptionHandler(InsufficientStockException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleInsufficientStock(InsufficientStockException e) {
    return Map.of("error", e.getMessage(), "type", "INSUFFICIENT_STOCK");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, String> handleGeneric(Exception e) {
    return Map.of("error", e.getMessage(), "type", "INTERNAL_ERROR");
  }
}
