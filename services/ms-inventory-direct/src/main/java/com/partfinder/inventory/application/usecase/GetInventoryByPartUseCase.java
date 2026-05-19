package com.partfinder.inventory.application.usecase;

import com.partfinder.inventory.domain.model.InventoryItem;
import com.partfinder.inventory.domain.model.PartId;
import com.partfinder.inventory.domain.port.out.InventoryRepository;

import java.util.List;

/**
 * Caso de uso: devolver la lista de proveedores que tienen un repuesto
 * (con su stock). Es lo que consulta MS-Aggregator de forma sincrona.
 */
public class GetInventoryByPartUseCase {

    private final InventoryRepository repository;

    public GetInventoryByPartUseCase(InventoryRepository repository) {
        this.repository = repository;
    }

    public List<InventoryItem> execute(PartId partId) {
        return repository.findByPart(partId);
    }
}
