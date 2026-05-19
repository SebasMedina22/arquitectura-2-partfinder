package com.partfinder.inventory.application.usecase;

import com.partfinder.inventory.domain.model.InventoryItem;
import com.partfinder.inventory.domain.model.PartId;
import com.partfinder.inventory.domain.model.StockQuantity;
import com.partfinder.inventory.domain.model.SupplierId;
import com.partfinder.inventory.domain.port.out.InventoryRepository;

import java.time.Clock;
import java.time.Instant;

/**
 * Caso de uso admin: setear el stock de (parte, proveedor). Si no existe lo crea.
 * En produccion real seria una integracion con cada bodega; aqui sirve para la demo.
 */
public class UpsertInventoryUseCase {

    private final InventoryRepository repository;
    private final Clock clock;

    public UpsertInventoryUseCase(InventoryRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public InventoryItem execute(PartId partId, SupplierId supplierId, StockQuantity stock) {
        Instant now = Instant.now(clock);
        InventoryItem item = repository.find(partId, supplierId)
                .orElseGet(() -> new InventoryItem(partId, supplierId, stock, now));
        item.updateStock(stock, now);
        repository.save(item);
        return item;
    }
}
