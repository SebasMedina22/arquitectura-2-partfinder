package com.partfinder.aggregator.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WorkshopTest {

    @Test
    void hasExceededCredit_falseSiUsadoMenorAlLimite() {
        Workshop w = new Workshop(new WorkshopId("WS-001"), "Test",
                Money.cop(new BigDecimal("1000000")),
                Money.cop(new BigDecimal("500000")));
        assertFalse(w.hasExceededCredit());
    }

    @Test
    void hasExceededCredit_trueSiUsadoIgualAlLimite() {
        Workshop w = new Workshop(new WorkshopId("WS-001"), "Test",
                Money.cop(new BigDecimal("1000000")),
                Money.cop(new BigDecimal("1000000")));
        assertTrue(w.hasExceededCredit());
    }

    @Test
    void hasExceededCredit_trueSiUsadoSuperaLimite() {
        Workshop w = new Workshop(new WorkshopId("WS-001"), "Test",
                Money.cop(new BigDecimal("1000000")),
                Money.cop(new BigDecimal("1500000")));
        assertTrue(w.hasExceededCredit());
    }

    @Test
    void chargeAdditional_sumaAlCreditoUsado() {
        Workshop w = new Workshop(new WorkshopId("WS-001"), "Test",
                Money.cop(new BigDecimal("1000000")),
                Money.cop(new BigDecimal("100000")));
        w.chargeAdditional(Money.cop(new BigDecimal("50000")));
        assertEquals(0, w.creditUsed().amount().compareTo(new BigDecimal("150000")));
    }
}
