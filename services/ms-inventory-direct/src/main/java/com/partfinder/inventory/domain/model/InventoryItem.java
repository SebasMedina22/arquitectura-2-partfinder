package com.partfinder.inventory.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad: unidad de inventario = una combinacion (parte, proveedor) con su stock actual.
 * El identificador conceptual es el par (partId, supplierId). Un proveedor puede tener
 * varias partes; una parte puede estar en varios proveedores.
 */
public class InventoryItem {

    private final PartId partId;
    private final SupplierId supplierId;
    private StockQuantity stock;
    private Instant lastUpdated;

    public InventoryItem(PartId partId, SupplierId supplierId, StockQuantity stock, Instant lastUpdated) {
        this.partId = Objects.requireNonNull(partId);
        this.supplierId = Objects.requireNonNull(supplierId);
        this.stock = Objects.requireNonNull(stock);
        this.lastUpdated = Objects.requireNonNull(lastUpdated);
    }

    public void updateStock(StockQuantity newStock, Instant at) {
        this.stock = Objects.requireNonNull(newStock);
        this.lastUpdated = Objects.requireNonNull(at);
    }

    public PartId partId() { return partId; }
    public SupplierId supplierId() { return supplierId; }
    public StockQuantity stock() { return stock; }
    public Instant lastUpdated() { return lastUpdated; }

    public boolean isAvailable() { return stock.isAvailable(); }
}
