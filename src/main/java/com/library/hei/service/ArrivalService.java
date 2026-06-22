package com.library.hei.service;

import com.library.hei.model.entity.Arrival;
import com.library.hei.model.entity.BookFormat;
import com.library.hei.model.entity.StockMovement;
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
  // NOUVEAU : injection du service de mouvements de stock
  private final StockMovementService stockMovementService;

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

    BookFormat format =
        bookFormatRepository
            .findById(formatId)
            .orElseThrow(() -> new NotFoundException("Format id=" + formatId + " introuvable"));

    // Incrémenter le stock du format concerné (comportement inchangé)
    format.setStock(format.getStock() + arrival.getQuantity());
    bookFormatRepository.save(format);

    // Sauvegarder l'arrivage en premier pour obtenir son ID généré
    Arrival saved = arrivalRepository.save(arrival);

    // NOUVEAU : enregistrer le mouvement de stock
    // quantity POSITIVE = entrée de stock
    // referenceId = id de cet arrivage, pour pouvoir retracer l'origine
    stockMovementService.record(
        format,
        StockMovement.MovementType.ARRIVAL,
        arrival.getQuantity(), // ex: +5
        saved.getId() // lien vers cet arrivage
        );

    return saved;
  }
}
