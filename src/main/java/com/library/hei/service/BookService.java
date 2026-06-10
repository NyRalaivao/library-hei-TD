package com.library.hei.service;

import com.library.hei.model.entity.*;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class BookService {

  private final BookRepository bookRepository;
  private final GenreRepository genreRepository;
  private final AuthorRepository authorRepository;

  public List<Book> getAll(int page, int pageSize) {
    return bookRepository.findAll(PageRequest.of(page, pageSize)).getContent();
  }

  public Book getById(String id) {
    return bookRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Livre id=" + id + " introuvable"));
  }

  public Book getByIsbn(String isbn) {
    return bookRepository.findByIsbn(isbn)
        .orElseThrow(() -> new NotFoundException("Livre ISBN=" + isbn + " introuvable"));
  }

  public List<Book> getLowStock(int threshold) {
    return bookRepository.findLowStockBooks(threshold, PageRequest.of(0, 50));
  }

  public List<Book> searchByTitle(String prefix, int page, int pageSize) {
    return bookRepository.findByTitleStartingWith(prefix, PageRequest.of(page, pageSize));
  }

  @Transactional
  public Book crupdate(String id, Book book) {
    validate(book);
    book.setId(id);
    return bookRepository.save(book);
  }

  @Transactional
  public Book delete(String id) {
    Book book = getById(id);
    bookRepository.deleteById(id);
    return book;
  }

  private void validate(Book book) {
    if (book.getTitle() == null || book.getTitle().isBlank())
      throw new BadRequestException("Le titre du livre est obligatoire");
    if (book.getPrice() == null || book.getPrice().signum() < 0)
      throw new BadRequestException("Le prix doit être positif ou nul");
  }
}
