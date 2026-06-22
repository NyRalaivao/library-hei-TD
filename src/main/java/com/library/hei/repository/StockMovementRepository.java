package com.library.hei.repository;

import com.library.hei.model.entity.StockMovement;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, String> {

  /**
   * Tous les mouvements d'un format donné, du plus récent au plus ancien. Utilisé par l'endpoint
   * GET /stock-movements/{formatId}
   */
  List<StockMovement> findByBookFormatIdOrderByMovementDateDesc(String formatId);

  /**
   * Tous les mouvements liés à une entité source (Arrival ou Sale). Ex :
   * findByReferenceId("sale-123") retourne tous les mouvements de stock générés par la vente
   * "sale-123".
   */
  List<StockMovement> findByReferenceId(String referenceId);

  /**
   * Calcule le stock dynamiquement en sommant tous les mouvements d'un format.
   *
   * <p>C'est la clé de l'approche "stock calculé" : on ne lit pas BookFormat.stock (qui pourrait
   * être désynchronisé), on somme les +/- de tous les mouvements.
   *
   * <p>Analogie : plutôt que de regarder le solde affiché sur votre carte, on additionne tous les
   * débits et crédits depuis l'ouverture du compte.
   */
  @Query(
      "SELECT COALESCE(SUM(m.quantity), 0) FROM StockMovement m "
          + "WHERE m.bookFormat.id = :formatId")
  int calculateStockByFormatId(String formatId);
}
