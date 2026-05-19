package com.partfinder.inventory.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueObjectsTest {

    @Test
    void partId_aceptaFormatoValido() {
        assertEquals("PRT-FO-001", new PartId("PRT-FO-001").value());
    }

    @Test
    void partId_normalizaAMayusculas() {
        assertEquals("PRT-FO-001", new PartId("prt-fo-001").value());
    }

    @Test
    void partId_rechazaInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new PartId("part-1"));
        assertThrows(IllegalArgumentException.class, () -> new PartId("PRT-"));
        assertThrows(NullPointerException.class, () -> new PartId(null));
    }

    @Test
    void supplierId_aceptaFormatoValido() {
        assertEquals("SUP-LIMA", new SupplierId("SUP-LIMA").value());
    }

    @Test
    void stockQuantity_noPermiteNegativos() {
        assertThrows(IllegalArgumentException.class, () -> new StockQuantity(-1));
    }

    @Test
    void stockQuantity_isAvailableSoloSiMayorACero() {
        assertTrue(StockQuantity.of(1).isAvailable());
        assertFalse(StockQuantity.zero().isAvailable());
    }
}
