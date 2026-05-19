package com.partfinder.trends.domain.event;

import java.time.Instant;

/**
 * Evento entrante publicado por MS-Aggregator cuando una busqueda devuelve NOT_FOUND.
 * Soporta la regla R3 desde el lado consumidor.
 *
 * Contrato JSON acordado (Aggregator es el publicador autoritativo).
 */
public record SearchFailedEvent(
        String eventId,        // UUID generado por Aggregator, sirve de idempotency key
        String partQuery,      // texto libre tal como lo busco el taller
        String workshopId,     // taller que hizo la busqueda
        Instant searchedAt
) {
}
