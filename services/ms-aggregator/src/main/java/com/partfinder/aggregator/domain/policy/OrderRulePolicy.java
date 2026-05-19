package com.partfinder.aggregator.domain.policy;

import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Workshop;

/**
 * Patron Strategy (GoF): contrato comun para reglas que validan la creacion de
 * un pedido. Hoy aplicamos una (R2 — credito); manana podriamos sumar (ej.
 * limite de items por dia) sin tocar el caso de uso.
 */
public interface OrderRulePolicy {

    /** Lanza OrderRuleViolationException si la regla bloquea el pedido. */
    void check(Workshop workshop, Money orderAmount);

    String ruleCode();
}
