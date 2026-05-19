package com.partfinder.aggregator.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.partfinder.aggregator.domain.event.SearchFailedEvent;
import com.partfinder.aggregator.domain.port.out.DomainEventPublisher;
import com.partfinder.aggregator.infrastructure.persistence.jpa.OutboxEventJpaEntity;
import com.partfinder.aggregator.infrastructure.persistence.repository.OutboxEventJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

/**
 * Patron Observer (GoF) implementado como Transactional Outbox.
 * Persiste el evento de dominio en outbox_events dentro de la misma transaccion
 * JPA que originaron la operacion (R3). Luego OutboxRelayWorker lo despacha.
 */
@Component
public class OutboxDomainEventPublisher implements DomainEventPublisher {

    private final OutboxEventJpaRepository outboxRepo;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public OutboxDomainEventPublisher(OutboxEventJpaRepository outboxRepo,
                                      ObjectMapper objectMapper,
                                      Clock clock) {
        this.outboxRepo = outboxRepo;
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    public void publish(SearchFailedEvent event) {
        String json;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar SearchFailedEvent", e);
        }
        outboxRepo.save(new OutboxEventJpaEntity(
                event.eventId(),
                "SearchFailed",
                RabbitMqConfig.SEARCH_FAILED_ROUTING_KEY,
                json,
                Instant.now(clock)
        ));
    }
}
