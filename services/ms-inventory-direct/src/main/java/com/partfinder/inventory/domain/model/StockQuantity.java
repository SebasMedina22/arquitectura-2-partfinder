package com.partfinder.inventory.domain.model;

import java.util.Objects;

/**
 * Value Object: cantidad en stock.
 * Invariante defensiva: no negativo.
 */
public final class StockQuantity {

    private final int value;

    public StockQuantity(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("StockQuantity no puede ser negativo: " + value);
        }
        this.value = value;
    }

    public static StockQuantity of(int v) { return new StockQuantity(v); }
    public static StockQuantity zero() { return new StockQuantity(0); }

    public int value() { return value; }
    public boolean isAvailable() { return value > 0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockQuantity other)) return false;
        return value == other.value;
    }

    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
