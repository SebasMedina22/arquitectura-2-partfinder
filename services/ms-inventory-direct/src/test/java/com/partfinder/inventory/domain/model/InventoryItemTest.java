package com.partfinder.inventory.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class InventoryItemTest {

    @Test
    void crear_yActualizar_stock() {
        Instant t0 = Instant.parse("2026-05-16T10:00:00Z");
        Instant t1 = Instant.parse("2026-05-16T10:05:00Z");
        InventoryItem it = new InventoryItem(
                new PartId("PRT-FO-001"), new SupplierId("SUP-LIMA"),
                StockQuantity.of(5), t0);
        assertTrue(it.isAvailable());
        it.updateStock(StockQuantity.zero(), t1);
        assertFalse(it.isAvailable());
        assertEquals(t1, it.lastUpdated());
    }
}
