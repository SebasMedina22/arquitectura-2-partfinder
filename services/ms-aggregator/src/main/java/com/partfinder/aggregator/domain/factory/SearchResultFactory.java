package com.partfinder.aggregator.domain.factory;

import com.partfinder.aggregator.domain.model.Availability;
import com.partfinder.aggregator.domain.model.InventoryQueryResult;
import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Part;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.SearchResult;

import java.util.List;
import java.util.Optional;

/**
 * Patron Factory (GoF). Centraliza la logica de "como construir el resultado
 * de busqueda con el availability correcto" segun:
 *
 *  - si InventoryDirect respondio en menos de 800ms (R1)
 *  - si hay stock en al menos un proveedor
 *  - si la parte esta en el catalogo de ES
 *
 * Esto evita esparcir la decision en multiples puntos del caso de uso.
 */
public class SearchResultFactory {

    /**
     * Construye el SearchResult final. Si la parte no esta en el catalogo Y
     * tampoco hay stock, retorna NOT_FOUND. Si InventoryDirect timed out,
     * retorna UNCERTAIN. Si todo OK con stock, retorna AVAILABLE.
     */
    public SearchResult build(PartId partId, Optional<Part> catalogPart, InventoryQueryResult inventoryResult) {
        // R1: si el sincrono fallo o tardo, marcamos UNCERTAIN
        if (inventoryResult.timedOut()) {
            return new SearchResult(
                    partId,
                    catalogPart.map(Part::name).orElse("(sin nombre)"),
                    catalogPart.map(Part::referencePrice).orElse(Money.zero("COP")),
                    Availability.UNCERTAIN,
                    inventoryResult.offers().stream()
                            .map(o -> new SearchResult.SupplierOffer(o.supplierId(), o.stock()))
                            .toList()
            );
        }

        // Si no esta en catalogo Y no hay stock => NOT_FOUND
        if (catalogPart.isEmpty() && !inventoryResult.hasAnyStock()) {
            return new SearchResult(partId, "(no encontrado)", Money.zero("COP"),
                    Availability.NOT_FOUND, List.of());
        }

        // En cualquier otro caso lo damos como AVAILABLE (puede que haya stock 0 en
        // todos pero el catalogo si conoce la parte: el cliente decide)
        boolean hayStock = inventoryResult.hasAnyStock();
        return new SearchResult(
                partId,
                catalogPart.map(Part::name).orElse("(sin nombre)"),
                catalogPart.map(Part::referencePrice).orElse(Money.zero("COP")),
                hayStock ? Availability.AVAILABLE : Availability.NOT_FOUND,
                inventoryResult.offers().stream()
                        .map(o -> new SearchResult.SupplierOffer(o.supplierId(), o.stock()))
                        .toList()
        );
    }
}
