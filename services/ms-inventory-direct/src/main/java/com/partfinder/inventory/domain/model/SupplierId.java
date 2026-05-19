package com.partfinder.inventory.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: identificador de un proveedor (bodega).
 * Formato SUP-XXX.
 */
public final class SupplierId {

    private static final Pattern PATTERN = Pattern.compile("^SUP-[A-Z0-9-]{1,32}$");

    private final String value;

    public SupplierId(String value) {
        Objects.requireNonNull(value, "SupplierId no puede ser null");
        String upper = value.toUpperCase();
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException("SupplierId invalido: " + value + " (esperado SUP-XXX)");
        }
        this.value = upper;
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SupplierId other)) return false;
        return value.equals(other.value);
    }

    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
