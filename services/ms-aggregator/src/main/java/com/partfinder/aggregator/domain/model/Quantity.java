package com.partfinder.aggregator.domain.model;

import java.util.Objects;

public final class Quantity {

    private final int value;

    public Quantity(int value) {
        if (value <= 0) throw new IllegalArgumentException("Quantity debe ser positivo: " + value);
        this.value = value;
    }

    public static Quantity of(int v) { return new Quantity(v); }

    public int value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity other)) return false;
        return value == other.value;
    }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return String.valueOf(value); }
}
