package com.library.hei.service;

import com.library.hei.model.entity.Arrival;
import com.library.hei.model.entity.BookFormat;
import com.library.hei.model.exception.BadRequestException;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.ArrivalRepository;
import com.library.hei.repository.BookFormatRepository;
import com.library.hei.repository.BookRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ArrivalService {

  private final ArrivalRepository arrivalRepository;
  private final BookRepository bookRepository;
  private final BookFormatRepository bookFormatRepository;

  public List<Arrival> getAll() {
    return arrivalRepository.findAll();
  }

  public List<Arrival> getByBook(String bookId) {
    return arrivalRepository.findByBookIdOrderByArrivalDateDesc(bookId);
  }

  @Transactional
  public Arrival createArrival(Arrival arrival, String formatId) {
    if (arrival.getQuantity() == null || arrival.getQuantity() <= 0)
      throw new BadRequestException("La quantité doit être positive");

    var book =
        bookRepository
            .findById(arrival.getBook().getId())
            .orElseThrow(() -> new NotFoundException("Livre introuvable"));
    arrival.setBook(book);

    // Incrémenter le stock du format concerné
    BookFormat format =
        bookFormatRepository
            .findById(formatId)
            .orElseThrow(() -> new NotFoundException("Format id=" + formatId + " introuvable"));
    format.setStock(format.getStock() + arrival.getQuantity());
    bookFormatRepository.save(format);

    return arrivalRepository.save(arrival);
  }
}
