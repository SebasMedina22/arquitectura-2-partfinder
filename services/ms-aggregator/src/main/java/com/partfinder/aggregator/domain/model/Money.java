package com.partfinder.aggregator.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object: monto monetario. Invariante: no negativo, 2 decimales.
 */
public final class Money {

    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);
        if (amount.signum() < 0) throw new IllegalArgumentException("Money negativo no permitido: " + amount);
        if (currency.length() != 3) throw new IllegalArgumentException("Currency debe ser ISO 4217 (3 letras): " + currency);
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency.toUpperCase();
    }

    public static Money cop(BigDecimal v) { return new Money(v, "COP"); }
    public static Money zero(String currency) { return new Money(BigDecimal.ZERO, currency); }

    public Money plus(Money other) {
        if (!currency.equals(other.currency)) throw new IllegalArgumentException("Currency mismatch");
        return new Money(amount.add(other.amount), currency);
    }

    public boolean greaterThan(Money other) {
        if (!currency.equals(other.currency)) throw new IllegalArgumentException("Currency mismatch");
        return amount.compareTo(other.amount) > 0;
    }

    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money other)) return false;
        return amount.compareTo(other.amount) == 0 && currency.equals(other.currency);
    }
    @Override public int hashCode() { return Objects.hash(amount.stripTrailingZeros(), currency); }
    @Override public String toString() { return amount.toPlainString() + " " + currency; }
}
