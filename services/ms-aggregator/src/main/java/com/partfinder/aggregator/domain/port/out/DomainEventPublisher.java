package com.partfinder.aggregator.domain.port.out;

import com.partfinder.aggregator.domain.event.SearchFailedEvent;

/**
 * Patron Observer (GoF) a nivel de dominio. La implementacion concreta es el
 * OutboxDomainEventPublisher: persiste el evento en outbox_events dentro de la
 * misma transaccion que lo origina. Asi R3 esta garantizado incluso si el broker
 * esta caido en el momento.
 */
public interface DomainEventPublisher {
    void publish(SearchFailedEvent event);
}
