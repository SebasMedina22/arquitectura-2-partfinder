package com.partfinder.inventory.infrastructure.persistence.repository;

import com.partfinder.inventory.infrastructure.persistence.jpa.InventoryItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemJpaRepository extends JpaRepository<InventoryItemJpaEntity, InventoryItemJpaEntity.Id> {
    List<InventoryItemJpaEntity> findByIdPartId(String partId);
}
