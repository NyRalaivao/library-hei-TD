package com.library.hei.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

/**
 * Enregistre chaque modification du stock d'un format de livre.
 *
 * <p>Analogie : c'est le "relevé de compte" du stock. Au lieu de juste voir le solde actuel
 * (BookFormat.stock), on garde trace de chaque entrée/sortie.
 *
 * <p>Exemples :
 *
 * <ul>
 *   <li>Arrivage de 10 exemplaires → quantity = +10, type = ARRIVAL
 *   <li>Vente de 2 exemplaires → quantity = -2, type = SALE
 *   <li>Annulation d'une vente de 2 → quantity = +2, type = CANCELLATION
 * </ul>
 */
@Entity
@Table(name = "stock_movement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  /** Le format de livre dont le stock a changé (ex: "Harry Potter - Poche"). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_format", nullable = false)
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private BookFormat bookFormat;

  /** Nature du mouvement : arrivage, vente confirmée, ou annulation. */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MovementType type;

  /**
   * Quantité modifiée. Valeur POSITIVE = entrée de stock (ARRIVAL, CANCELLATION). Valeur NÉGATIVE =
   * sortie de stock (SALE).
   */
  @Column(nullable = false)
  private Integer quantity;

  /**
   * Identifiant de l'entité à l'origine du mouvement. Permet de retrouver tous les mouvements liés
   * à une vente ou un arrivage. Ex : si referenceId = "sale-123", c'est la vente "sale-123" qui a
   * déclenché ce mouvement.
   */
  @Column(name = "reference_id")
  private String referenceId;

  /** Date et heure exactes du mouvement. Auto-rempli si null. */
  @Column(name = "movement_date", nullable = false)
  private LocalDateTime movementDate;

  @PrePersist
  public void prePersist() {
    if (movementDate == null) movementDate = LocalDateTime.now();
  }

  public enum MovementType {
    /** Réception de nouveaux exemplaires (via ArrivalService). */
    ARRIVAL,
    /** Décrément suite à une vente confirmée (via SaleService.confirmSale). */
    SALE,
    /** Remise en stock suite à l'annulation d'une vente PENDING. */
    CANCELLATION
  }
}
