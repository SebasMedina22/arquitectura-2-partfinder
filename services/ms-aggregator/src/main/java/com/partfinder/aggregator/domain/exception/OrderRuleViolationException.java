package com.partfinder.aggregator.domain.exception;

/**
 * Se lanza cuando una de las reglas de negocio del caso (hoy R2: cupo de credito
 * excedido) bloquea la creacion de un pedido. Mapeado a HTTP 422.
 */
public class OrderRuleViolationException extends RuntimeException {
    private final String ruleCode;

    public OrderRuleViolationException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public String ruleCode() { return ruleCode; }
}
