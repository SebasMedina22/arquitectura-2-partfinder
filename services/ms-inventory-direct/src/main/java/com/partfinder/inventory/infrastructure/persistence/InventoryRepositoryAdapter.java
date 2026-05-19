package com.partfinder.inventory.infrastructure.persistence;

import com.partfinder.inventory.domain.model.InventoryItem;
import com.partfinder.inventory.domain.model.PartId;
import com.partfinder.inventory.domain.model.StockQuantity;
import com.partfinder.inventory.domain.model.SupplierId;
import com.partfinder.inventory.domain.port.out.InventoryRepository;
import com.partfinder.inventory.infrastructure.persistence.jpa.InventoryItemJpaEntity;
import com.partfinder.inventory.infrastructure.persistence.repository.InventoryItemJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Patron Adapter (GoF) lado salida: traduce el puerto del dominio
 * (InventoryRepository) a Spring Data JPA. El dominio no conoce JPA.
 */
@Component
public class InventoryRepositoryAdapter implements InventoryRepository {

    private final InventoryItemJpaRepository jpa;

    public InventoryRepositoryAdapter(InventoryItemJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<InventoryItem> find(PartId partId, SupplierId supplierId) {
        return jpa.findById(new InventoryItemJpaEntity.Id(partId.value(), supplierId.value()))
                .map(this::toDomain);
    }

    @Override
    public List<InventoryItem> findByPart(PartId partId) {
        return jpa.findByIdPartId(partId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public void save(InventoryItem item) {
        InventoryItemJpaEntity entity = jpa.findById(new InventoryItemJpaEntity.Id(
                item.partId().value(), item.supplierId().value()
        )).orElseGet(() -> new InventoryItemJpaEntity(
                item.partId().value(), item.supplierId().value(),
                item.stock().value(), item.lastUpdated()
        ));
        entity.setStock(item.stock().value());
        entity.setLastUpdated(item.lastUpdated());
        jpa.save(entity);
    }

    private InventoryItem toDomain(InventoryItemJpaEntity e) {
        return new InventoryItem(
                new PartId(e.getId().getPartId()),
                new SupplierId(e.getId().getSupplierId()),
                StockQuantity.of(e.getStock()),
                e.getLastUpdated()
        );
    }
}
