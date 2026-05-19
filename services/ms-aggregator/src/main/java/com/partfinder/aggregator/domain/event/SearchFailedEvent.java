package com.partfinder.aggregator.domain.event;

import java.time.Instant;

/**
 * Evento saliente: una busqueda devolvio NOT_FOUND. Materializa R3 del caso 7.
 *
 * Se persiste primero en outbox_events dentro de la misma transaccion que confirma
 * la busqueda. El OutboxRelayWorker lo despacha al broker; TrendCollector lo consume.
 *
 * Contrato JSON acordado con MS-TrendCollector (routing key "search.failed" en
 * el exchange "partfinder.search.events").
 */
public record SearchFailedEvent(
        String eventId,
        String partQuery,
        String workshopId,
        Instant searchedAt
) {
}
