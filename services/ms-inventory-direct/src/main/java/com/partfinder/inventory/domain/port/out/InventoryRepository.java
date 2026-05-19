package com.partfinder.inventory.domain.port.out;

import com.partfinder.inventory.domain.model.InventoryItem;
import com.partfinder.inventory.domain.model.PartId;
import com.partfinder.inventory.domain.model.SupplierId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida del dominio hacia el store. Implementado por un Adapter JPA.
 */
public interface InventoryRepository {

    Optional<InventoryItem> find(PartId partId, SupplierId supplierId);

    List<InventoryItem> findByPart(PartId partId);

    void save(InventoryItem item);
}
