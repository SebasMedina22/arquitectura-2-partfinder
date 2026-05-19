package com.partfinder.trends.domain.model;

import java.util.Objects;

/**
 * Value Object: la query de busqueda que un taller intento (libre, ej. "filtro de aceite toyota").
 * Invariante: no nulo, no vacio, normalizado a lowercase + trim para agrupar duplicados.
 */
public final class PartQuery {

    private final String value;

    public PartQuery(String value) {
        Objects.requireNonNull(value, "PartQuery no puede ser null");
        String normalized = value.trim().toLowerCase();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("PartQuery no puede ser vacio");
        }
        if (normalized.length() > 200) {
            throw new IllegalArgumentException("PartQuery excede 200 caracteres");
        }
        this.value = normalized;
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartQuery other)) return false;
        return value.equals(other.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
