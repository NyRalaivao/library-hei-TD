package com.library.hei.service;

import com.library.hei.model.entity.BookFormat;
import com.library.hei.model.entity.StockMovement;
import com.library.hei.model.exception.NotFoundException;
import com.library.hei.repository.BookFormatRepository;
import com.library.hei.repository.StockMovementRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class StockMovementService {

  private final StockMovementRepository stockMovementRepository;
  private final BookFormatRepository bookFormatRepository;

  /**
   * Retourne l'historique complet des mouvements de stock pour un format donné, du plus récent au
   * plus ancien.
   *
   * <p>Erreur fréquente de débutant : oublier de vérifier que le format existe avant de faire la
   * requête → on aurait une liste vide sans message clair.
   */
  public List<StockMovement> getHistoryByFormat(String formatId) {
    // On vérifie que le format existe (sinon on lève une erreur explicite)
    if (!bookFormatRepository.existsById(formatId)) {
      throw new NotFoundException("Format id=" + formatId + " introuvable");
    }
    return stockMovementRepository.findByBookFormatIdOrderByMovementDateDesc(formatId);
  }

  /**
   * Retourne tous les mouvements liés à une source donnée (ex: une Sale ou une Arrival). Utile pour
   * déboguer : "qu'est-ce que la vente X a fait au stock ?"
   */
  public List<StockMovement> getByReference(String referenceId) {
    return stockMovementRepository.findByReferenceId(referenceId);
  }

  /**
   * Calcule le stock réel d'un format en sommant tous ses mouvements.
   *
   * <p>AVANTAGE par rapport à BookFormat.stock : Le résultat est toujours exact car il repart des
   * données brutes. Si BookFormat.stock est désynchronisé (bug, mise à jour manuelle en BDD...),
   * cette méthode donnera quand même la bonne valeur.
   *
   * <p>INCONVÉNIENT : plus lente car elle fait un SUM() en base à chaque appel. En pratique, ça
   * reste rapide tant que la table stock_movement n'a pas des millions de lignes pour un même
   * format.
   */
  public int calculateCurrentStock(String formatId) {
    if (!bookFormatRepository.existsById(formatId)) {
      throw new NotFoundException("Format id=" + formatId + " introuvable");
    }
    return stockMovementRepository.calculateStockByFormatId(formatId);
  }

  /**
   * Synchronise BookFormat.stock avec le stock calculé depuis les mouvements.
   *
   * <p>À appeler si vous suspectez une désynchronisation entre la colonne stock et les mouvements
   * enregistrés. Utile aussi pour la migration initiale (juste après avoir introduit StockMovement
   * dans un projet existant).
   *
   * <p>Bonne pratique en entreprise : cette opération est souvent déclenchée par un job planifié
   * (cron) la nuit, pour s'assurer de la cohérence des données.
   */
  @Transactional
  public void resyncStock(String formatId) {
    BookFormat format =
        bookFormatRepository
            .findById(formatId)
            .orElseThrow(() -> new NotFoundException("Format id=" + formatId + " introuvable"));

    int calculatedStock = stockMovementRepository.calculateStockByFormatId(formatId);
    format.setStock(calculatedStock); // on écrase le stock avec la valeur calculée
    bookFormatRepository.save(format);
  }

  /**
   * Méthode utilitaire interne : crée et persiste un mouvement de stock. Appelée par ArrivalService
   * et SaleService pour centraliser la logique.
   *
   * <p>Bonne pratique : on passe l'entité BookFormat (déjà chargée) plutôt que de la recharger
   * depuis la BDD → évite une requête SELECT inutile.
   */
  @Transactional
  public StockMovement record(
      BookFormat format, StockMovement.MovementType type, int quantity, String referenceId) {
    StockMovement movement =
        StockMovement.builder()
            .bookFormat(format)
            .type(type)
            .quantity(quantity)
            .referenceId(referenceId)
            .build();
    return stockMovementRepository.save(movement);
  }
}
