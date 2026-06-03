package com.partfinder.trends.infrastructure.messaging;

import com.partfinder.trends.application.usecase.RecordFailedSearchUseCase;
import com.partfinder.trends.domain.event.SearchFailedEvent;
import com.partfinder.trends.infrastructure.ws.TrendWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Patron Adapter (GoF) entrante: traduce un mensaje AMQP al caso de uso de dominio.
 * Si el caso de uso falla con error no-recoverable, el mensaje cae a la DLQ.
 * Tras registrar un evento NUEVO, lo empuja por WebSocket a la UI (tendencias en vivo).
 */
@Component
public class RabbitSearchFailedConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitSearchFailedConsumer.class);

    private final RecordFailedSearchUseCase useCase;
    private final TrendWebSocketHandler ws;

    public RabbitSearchFailedConsumer(RecordFailedSearchUseCase useCase, TrendWebSocketHandler ws) {
        this.useCase = useCase;
        this.ws = ws;
    }

    @RabbitListener(queues = RabbitMqConfig.TRENDS_FAILED_QUEUE)
    public void onMessage(SearchFailedEvent event) {
        log.info("SearchFailedEvent recibido eventId={} query='{}' workshop={}",
                event.eventId(), event.partQuery(), event.workshopId());
        boolean isNew = useCase.execute(event);
        if (isNew) {
            // Best-effort: un fallo de WS no debe nack-ear el mensaje ni mandarlo a la DLQ.
            try { ws.broadcast(event); }
            catch (RuntimeException e) { log.warn("Broadcast WS fallo: {}", e.getMessage()); }
        }
    }
}
