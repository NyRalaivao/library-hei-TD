package com.library.hei.service;

import com.library.hei.model.entity.BookFormat;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.BookFormatRepository;
import com.library.hei.repository.BookRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookFormatService {

  private final BookFormatRepository bookFormatRepository;
  private final BookRepository bookRepository;

  /**
   * Retourne le stock total d'un livre, toutes éditions/formats confondus.
   *
   * @throws NotFoundException si le livre n'existe pas
   */
  public int getBookTotalStock(String bookId) {
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Livre id=" + bookId + " introuvable");
    }
    return bookFormatRepository.findByBookId(bookId).stream().mapToInt(BookFormat::getStock).sum();
  }

  public int getBookCopyStock(String bookId, String formatId) {
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Livre id=" + bookId + " introuvable");
    }

    BookFormat format =
        bookFormatRepository
            .findById(formatId)
            .orElseThrow(() -> new NotFoundException("Format id=" + formatId + " introuvable"));

    if (!format.getBook().getId().equals(bookId)) {
      throw new NotFoundException(
          "Format id=" + formatId + " introuvable pour le livre id=" + bookId);
    }

    return format.getStock();
  }

  public boolean hasEnoughStock(String formatId, int requestedQuantity) {
    if (requestedQuantity < 0) {
      throw new BadRequestException("La quantité demandée doit être positive");
    }
    BookFormat format =
        bookFormatRepository
            .findById(formatId)
            .orElseThrow(() -> new NotFoundException("Format id=" + formatId + " introuvable"));
    return format.getStock() >= requestedQuantity;
  }

  public List<BookFormat> getFormatsByBook(String bookId) {
    if (!bookRepository.existsById(bookId)) {
      throw new NotFoundException("Livre id=" + bookId + " introuvable");
    }
    return bookFormatRepository.findByBookId(bookId);
  }
}
