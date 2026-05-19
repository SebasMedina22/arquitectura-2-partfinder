package com.partfinder.aggregator.domain.policy;

import com.partfinder.aggregator.domain.exception.OrderRuleViolationException;
import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Workshop;
import com.partfinder.aggregator.domain.model.WorkshopId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderCreditPolicyTest {

    @Test
    void permite_cuandoPedidoCabeEnElCupoRestante() {
        Workshop w = new Workshop(new WorkshopId("WS-001"), "Solvente",
                Money.cop(new BigDecimal("1000000")),
                Money.cop(new BigDecimal("100000")));
        OrderCreditPolicy policy = new OrderCreditPolicy();
        assertDoesNotThrow(() -> policy.check(w, Money.cop(new BigDecimal("500000"))));
    }

    @Test
    void rechaza_cuandoYaEstaExcedido() {
        Workshop w = new Workshop(new WorkshopId("WS-003"), "Excedido",
                Money.cop(new BigDecimal("1000000")),
                Money.cop(new BigDecimal("1200000")));
        OrderCreditPolicy policy = new OrderCreditPolicy();
        OrderRuleViolationException ex = assertThrows(OrderRuleViolationException.class,
                () -> policy.check(w, Money.cop(new BigDecimal("50000"))));
        assertEquals("R2_CREDIT_EXCEEDED", ex.ruleCode());
    }

    @Test
    void rechaza_cuandoPedidoExcederiaElCupo() {
        Workshop w = new Workshop(new WorkshopId("WS-002"), "Cerca",
                Money.cop(new BigDecimal("1000000")),
                Money.cop(new BigDecimal("900000")));
        OrderCreditPolicy policy = new OrderCreditPolicy();
        assertThrows(OrderRuleViolationException.class,
                () -> policy.check(w, Money.cop(new BigDecimal("200000"))));
    }
}
