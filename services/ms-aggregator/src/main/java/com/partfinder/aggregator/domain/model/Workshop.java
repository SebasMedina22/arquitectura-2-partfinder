package com.partfinder.aggregator.domain.model;

import java.util.Objects;

/**
 * Entidad: taller mecanico cliente del marketplace. Representa la proyeccion
 * local de su estado financiero (cupo de credito + credito usado). Es el
 * sustituto del "microservicio financiero" mencionado en el enunciado del caso 7.
 */
public class Workshop {

    private final WorkshopId id;
    private final String name;
    private final Money creditLimit;
    private Money creditUsed;

    public Workshop(WorkshopId id, String name, Money creditLimit, Money creditUsed) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.creditLimit = Objects.requireNonNull(creditLimit);
        this.creditUsed = Objects.requireNonNull(creditUsed);
        if (!creditLimit.currency().equals(creditUsed.currency())) {
            throw new IllegalArgumentException("Currency mismatch entre creditLimit y creditUsed");
        }
    }

    /** Verdadero si esta el taller ya excedio su cupo (creditUsed >= creditLimit). */
    public boolean hasExceededCredit() {
        return creditUsed.amount().compareTo(creditLimit.amount()) >= 0;
    }

    /** Crece el credito usado por un nuevo pedido. */
    public void chargeAdditional(Money amount) {
        this.creditUsed = creditUsed.plus(amount);
    }

    public WorkshopId id() { return id; }
    public String name() { return name; }
    public Money creditLimit() { return creditLimit; }
    public Money creditUsed() { return creditUsed; }

    public Money creditAvailable() {
        return new Money(creditLimit.amount().subtract(creditUsed.amount()).max(java.math.BigDecimal.ZERO),
                creditLimit.currency());
    }
}
