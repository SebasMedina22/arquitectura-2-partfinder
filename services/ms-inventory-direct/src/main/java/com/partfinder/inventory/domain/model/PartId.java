package com.partfinder.inventory.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: identificador de un repuesto en el catalogo.
 * Formato PRT-XXX (alfanumerico, 1-32 caracteres). Invariante defensiva.
 */
public final class PartId {

    private static final Pattern PATTERN = Pattern.compile("^PRT-[A-Z0-9-]{1,32}$");

    private final String value;

    public PartId(String value) {
        Objects.requireNonNull(value, "PartId no puede ser null");
        String upper = value.toUpperCase();
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException("PartId invalido: " + value + " (esperado PRT-XXX)");
        }
        this.value = upper;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartId other)) return false;
        return value.equals(other.value);
    }

    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
