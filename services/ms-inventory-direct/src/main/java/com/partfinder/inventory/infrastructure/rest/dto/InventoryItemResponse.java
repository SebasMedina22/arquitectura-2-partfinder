package com.partfinder.inventory.infrastructure.rest.dto;

import com.partfinder.inventory.domain.model.InventoryItem;

import java.time.Instant;

public record InventoryItemResponse(
        String partId,
        String supplierId,
        int stock,
        boolean available,
        Instant lastUpdated
) {
    public static InventoryItemResponse from(InventoryItem item) {
        return new InventoryItemResponse(
                item.partId().value(),
                item.supplierId().value(),
                item.stock().value(),
                item.isAvailable(),
                item.lastUpdated()
        );
    }
}
