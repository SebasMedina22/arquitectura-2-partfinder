package com.partfinder.aggregator.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ValueObjectsTest {

    @Test
    void partId_validaFormato() {
        assertEquals("PRT-FO-001", new PartId("PRT-FO-001").value());
        assertThrows(IllegalArgumentException.class, () -> new PartId("part-1"));
    }

    @Test
    void workshopId_validaFormato() {
        assertEquals("WS-001", new WorkshopId("ws-001").value());
        assertThrows(IllegalArgumentException.class, () -> new WorkshopId("WORKSHOP-1"));
    }

    @Test
    void money_rechazaNegativos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(new BigDecimal("-1"), "COP"));
    }

    @Test
    void money_rechazaCurrencyInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> new Money(BigDecimal.TEN, "PESOS"));
    }

    @Test
    void money_redondeaA2Decimales() {
        assertEquals(new BigDecimal("12.35"),
                Money.cop(new BigDecimal("12.345")).amount());
    }

    @Test
    void money_sumaCheckaCurrency() {
        assertThrows(IllegalArgumentException.class,
                () -> Money.cop(BigDecimal.TEN).plus(new Money(BigDecimal.TEN, "USD")));
    }

    @Test
    void quantity_debeSerPositivo() {
        assertThrows(IllegalArgumentException.class, () -> Quantity.of(0));
        assertThrows(IllegalArgumentException.class, () -> Quantity.of(-1));
        assertEquals(5, Quantity.of(5).value());
    }
}
