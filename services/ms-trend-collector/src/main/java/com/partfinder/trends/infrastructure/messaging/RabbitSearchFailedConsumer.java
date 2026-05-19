package com.partfinder.trends.infrastructure.messaging;

import com.partfinder.trends.application.usecase.RecordFailedSearchUseCase;
import com.partfinder.trends.domain.event.SearchFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Patron Adapter (GoF) entrante: traduce un mensaje AMQP al caso de uso de dominio.
 * Si el caso de uso falla con error no-recoverable, el mensaje cae a la DLQ.
 */
@Component
public class RabbitSearchFailedConsumer {

    private static final Logger log = LoggerFactory.getLogger(RabbitSearchFailedConsumer.class);

    private final RecordFailedSearchUseCase useCase;

    public RabbitSearchFailedConsumer(RecordFailedSearchUseCase useCase) {
        this.useCase = useCase;
    }

    @RabbitListener(queues = RabbitMqConfig.TRENDS_FAILED_QUEUE)
    public void onMessage(SearchFailedEvent event) {
        log.info("SearchFailedEvent recibido eventId={} query='{}' workshop={}",
                event.eventId(), event.partQuery(), event.workshopId());
        useCase.execute(event);
    }
}
