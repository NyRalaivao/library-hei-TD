package com.library.hei.service;

import com.library.hei.model.entity.Book;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public List<Book> getBooks(int page, int pageSize, String titlePrefix,
      Instant createdBefore, Instant createdAfter) {
    Pageable pageable = PageRequest.of(page, pageSize);
    return bookRepository.findByFilters(titlePrefix, createdBefore, createdAfter, pageable);
  }

  public Book getBookById(String id) {
    return bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Book with id=" + id + " not found"));
  }

  public Book crupdateBook(String id, Book book) {
    validate(book);
    book.setId(id);
    if (book.getCreationDatetime() == null) {
      book.setCreationDatetime(Instant.now());
    }
    return bookRepository.save(book);
  }

  public Book importBook(String id) {
    Book original = getBookById(id);
    Book copy = Book.builder()
        .id(UUID.randomUUID().toString())
        .title("Copy of " + original.getTitle())
        .author(original.getAuthor())
        .pageNumbers(original.getPageNumbers())
        .topic(original.getTopic())
        .format(original.getFormat())
        .releaseDate(original.getReleaseDate())
        .creationDatetime(Instant.now())
        .build();
    return bookRepository.save(copy);
  }

  public Book deleteBook(String id) {
    Book book = getBookById(id);
    bookRepository.deleteById(id);
    return book;
  }

  private void validate(Book book) {
    if (book.getTitle() == null || book.getTitle().isBlank()) {
      throw new BadRequestException("Book title is required");
    }
    if (book.getAuthor() == null || book.getAuthor().isBlank()) {
      throw new BadRequestException("Book author is required");
    }
    if (book.getFormat() == null) {
      throw new BadRequestException("Book format is required");
    }
    if (book.getPageNumbers() != null && book.getPageNumbers() <= 0) {
      throw new BadRequestException("Page numbers must be positive");
    }
  }
}
