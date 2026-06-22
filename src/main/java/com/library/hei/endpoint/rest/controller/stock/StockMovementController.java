package com.library.hei.endpoint.rest.controller.stock;

import com.library.hei.model.entity.StockMovement;
import com.library.hei.service.StockMovementService;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints pour consulter l'historique des mouvements de stock.
 *
 * <p>Bonne pratique : on sépare les endpoints de lecture (GET) dans un controller dédié
 * plutôt que de les entasser dans BookFormatController ou SaleController.
 * Cela respecte le principe de responsabilité unique (SRP).
 */
@RestController
@AllArgsConstructor
@RequestMapping("/stock-movements")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    /**
     * GET /stock-movements/{formatId}
     * Retourne l'historique complet des mouvements de stock pour un format de livre.
     *
     * <p>Exemple de réponse :
     * [
     *   { "id": "...", "type": "ARRIVAL",      "quantity": 10,  "referenceId": "arrival-1", ... },
     *   { "id": "...", "type": "SALE",         "quantity": -2,  "referenceId": "sale-42",   ... },
     *   { "id": "...", "type": "CANCELLATION", "quantity": 2,   "referenceId": "sale-99",   ... }
     * ]
     */
    @GetMapping("/{formatId}")
    public List<StockMovement> getHistory(@PathVariable String formatId) {
        return stockMovementService.getHistoryByFormat(formatId);
    }

    /**
     * GET /stock-movements/{formatId}/calculated-stock
     * Retourne le stock calculé dynamiquement depuis les mouvements.
     *
     * <p>Différence avec GET /books/{bookId}/formats/{formatId}/stock :
     * - L'autre endpoint lit BookFormat.stock (valeur mise en cache en BDD)
     * - Celui-ci fait SUM(quantity) sur tous les mouvements → toujours exact
     *
     * <p>Utile pour vérifier la cohérence ou après une migration.
     */
    @GetMapping("/{formatId}/calculated-stock")
    public Map<String, Object> getCalculatedStock(@PathVariable String formatId) {
        int stock = stockMovementService.calculateCurrentStock(formatId);
        return Map.of("formatId", formatId, "calculatedStock", stock);
    }

    /**
     * GET /stock-movements/by-reference/{referenceId}
     * Retourne tous les mouvements liés à une entité source (Sale ou Arrival).
     *
     * <p>Exemple : GET /stock-movements/by-reference/sale-123
     * → retourne tous les mouvements de stock générés par la vente "sale-123"
     */
    @GetMapping("/by-reference/{referenceId}")
    public List<StockMovement> getByReference(@PathVariable String referenceId) {
        return stockMovementService.getByReference(referenceId);
    }
}