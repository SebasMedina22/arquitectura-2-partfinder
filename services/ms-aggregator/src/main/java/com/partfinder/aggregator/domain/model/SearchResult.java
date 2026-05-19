package com.partfinder.aggregator.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Resultado de una busqueda. Encapsula el availability (R1 lo marca como UNCERTAIN
 * cuando InventoryDirect tarda > 800ms o falla).
 */
public record SearchResult(
        PartId partId,
        String partName,
        Money referencePrice,
        Availability availability,
        List<SupplierOffer> supplierOffers
) {
    public SearchResult {
        Objects.requireNonNull(partId);
        Objects.requireNonNull(availability);
        Objects.requireNonNull(supplierOffers);
    }

    public record SupplierOffer(SupplierId supplierId, int stock) {}
}
