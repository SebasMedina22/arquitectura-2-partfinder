package com.partfinder.trends.application.usecase;

import com.partfinder.trends.domain.event.SearchFailedEvent;
import com.partfinder.trends.domain.model.FailedSearchRecord;
import com.partfinder.trends.domain.model.PartQuery;
import com.partfinder.trends.domain.model.WorkshopId;
import com.partfinder.trends.domain.port.out.FailedSearchRepository;

/**
 * Caso de uso aplicado por el consumer AMQP de SearchFailedEvent.
 * Idempotencia: si ya existe un registro con el mismo eventId, lo ignora.
 * Esto es defensa contra reintentos del broker.
 */
public class RecordFailedSearchUseCase {

    private final FailedSearchRepository repository;

    public RecordFailedSearchUseCase(FailedSearchRepository repository) {
        this.repository = repository;
    }

    public void execute(SearchFailedEvent event) {
        if (repository.findByEventId(event.eventId()).isPresent()) {
            return; // ya procesado
        }
        repository.save(new FailedSearchRecord(
                event.eventId(),
                new PartQuery(event.partQuery()),
                new WorkshopId(event.workshopId()),
                event.searchedAt()
        ));
    }
}
