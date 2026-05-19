package com.partfinder.aggregator.infrastructure.messaging;

import com.partfinder.aggregator.infrastructure.persistence.jpa.OutboxEventJpaEntity;
import com.partfinder.aggregator.infrastructure.persistence.repository.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Worker que cada poll-interval recoge eventos pendientes del outbox y los
 * publica al broker. Si la publicacion falla (broker caido), incrementa attempts
 * y reintenta en el proximo tick. Asi se sostiene R3 incluso bajo cortes.
 *
 * IMPORTANTE: publicamos los bytes JSON directamente con content-type=application/json.
 * Si usaramos convertAndSend(String), el Jackson converter envolveria el string en
 * comillas dobles y el consumer fallaria a deserializar (leccion VoltNet).
 */
@Component
public class OutboxRelayWorker {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayWorker.class);

    private final OutboxEventJpaRepository repo;
    private final RabbitTemplate rabbitTemplate;
    private final Clock clock;
    private final int batchSize;

    public OutboxRelayWorker(OutboxEventJpaRepository repo,
                             RabbitTemplate rabbitTemplate,
                             Clock clock,
                             @Value("${aggregator.outbox.batch-size:50}") int batchSize) {
        this.repo = repo;
        this.rabbitTemplate = rabbitTemplate;
        this.clock = clock;
        this.batchSize = batchSize;
    }

    @Scheduled(fixedDelayString = "${aggregator.outbox.poll-interval-ms:1000}")
    @Transactional
    public void relay() {
        List<OutboxEventJpaEntity> pending = repo.findPending(PageRequest.of(0, batchSize));
        if (pending.isEmpty()) return;

        for (OutboxEventJpaEntity e : pending) {
            try {
                Message msg = MessageBuilder
                        .withBody(e.getPayload().getBytes(StandardCharsets.UTF_8))
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .setContentEncoding(StandardCharsets.UTF_8.name())
                        .build();
                rabbitTemplate.send(
                        RabbitMqConfig.SEARCH_EXCHANGE,
                        e.getRoutingKey(),
                        msg
                );
                e.markPublished(Instant.now(clock));
                repo.save(e);
            } catch (AmqpException ex) {
                e.incrementAttempts();
                repo.save(e);
                log.warn("Outbox: fallo al publicar id={} type={} (intentos={}). Reintentara.",
                        e.getId(), e.getEventType(), e.getAttempts());
            }
        }
    }
}
