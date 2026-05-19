package com.partfinder.trends.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidad: una busqueda fallida registrada. Cada evento entrante crea una fila.
 * El campo `eventId` es la idempotency key (UNIQUE en DB).
 */
public class FailedSearchRecord {

    private final String eventId;
    private final PartQuery partQuery;
    private final WorkshopId workshopId;
    private final Instant searchedAt;

    public FailedSearchRecord(String eventId, PartQuery partQuery, WorkshopId workshopId, Instant searchedAt) {
        this.eventId = Objects.requireNonNull(eventId);
        if (eventId.isBlank()) throw new IllegalArgumentException("eventId no puede ser vacio");
        this.partQuery = Objects.requireNonNull(partQuery);
        this.workshopId = Objects.requireNonNull(workshopId);
        this.searchedAt = Objects.requireNonNull(searchedAt);
    }

    public String eventId() { return eventId; }
    public PartQuery partQuery() { return partQuery; }
    public WorkshopId workshopId() { return workshopId; }
    public Instant searchedAt() { return searchedAt; }
}
