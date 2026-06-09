package com.library.hei.endpoint.rest.controller.book;

import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Map<String, String> handleNotFound(NotFoundException e) {
    return Map.of("message", e.getMessage(), "type", "NotFoundException");
  }

  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleBadRequest(BadRequestException e) {
    return Map.of("message", e.getMessage(), "type", "BadRequestException");
  }
}
