package com.library.hei.endpoint.rest.controller;

import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.InsufficientStockException;
import com.library.hei.model.exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ExceptionBody> handleNotFound(
      NotFoundException exception, HttpServletRequest request) {

    return ResponseEntity.status(404)
        .body(
            new ExceptionBody(
                404, "NOT_FOUND", exception.getMessage(), request.getRequestURI(), Instant.now()));
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ExceptionBody> handleBadRequest(
      BadRequestException exception, HttpServletRequest request) {

    return ResponseEntity.badRequest()
        .body(
            new ExceptionBody(
                400,
                "BAD_REQUEST",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now()));
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<ExceptionBody> handleInsufficientStock(
      InsufficientStockException exception, HttpServletRequest request) {

    return ResponseEntity.badRequest()
        .body(
            new ExceptionBody(
                400,
                "INSUFFICIENT_STOCK",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionBody> handleException(
      Exception exception, HttpServletRequest request) {

    return ResponseEntity.status(500)
        .body(
            new ExceptionBody(
                500,
                "INTERNAL_SERVER_ERROR",
                exception.getMessage(),
                request.getRequestURI(),
                Instant.now()));
  }
}
