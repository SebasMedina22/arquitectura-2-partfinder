package com.partfinder.trends.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Snapshot agregado de cuantas veces se busco una query sin exito.
 * Sirve para alimentar al panel "Tendencias" y eventualmente a los proveedores.
 */
public record TrendSummary(
        PartQuery partQuery,
        long failCount,
        Instant firstSeen,
        Instant lastSeen
) {
    public TrendSummary {
        Objects.requireNonNull(partQuery);
        Objects.requireNonNull(firstSeen);
        Objects.requireNonNull(lastSeen);
        if (failCount < 0) throw new IllegalArgumentException("failCount no puede ser negativo");
    }
}
