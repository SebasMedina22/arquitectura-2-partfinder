package com.partfinder.aggregator.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Lo que devuelve el puerto InventoryDirectPort cuando consultamos por una parte.
 *
 * - offers: lista de proveedores con su stock (puede ser vacia si no hay nadie con stock)
 * - timedOut: true si la llamada al MS sincrono tardo mas del threshold (R1)
 */
public record InventoryQueryResult(List<Offer> offers, boolean timedOut) {

    public InventoryQueryResult {
        Objects.requireNonNull(offers);
    }

    public boolean hasAnyStock() {
        return offers.stream().anyMatch(o -> o.stock() > 0);
    }

    public record Offer(SupplierId supplierId, int stock) {}
}
