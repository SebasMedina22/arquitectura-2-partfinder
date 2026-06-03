package com.partfinder.trends.infrastructure.persistence;

import com.partfinder.trends.domain.model.FailedSearchRecord;
import com.partfinder.trends.domain.model.PartQuery;
import com.partfinder.trends.domain.model.TrendSummary;
import com.partfinder.trends.domain.model.WorkshopId;
import com.partfinder.trends.domain.port.out.FailedSearchRepository;
import com.partfinder.trends.infrastructure.persistence.jpa.FailedSearchJpaEntity;
import com.partfinder.trends.infrastructure.persistence.repository.FailedSearchJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Patron Adapter (GoF) lado salida: implementa el puerto del dominio con JPA.
 */
@Component
public class FailedSearchRepositoryAdapter implements FailedSearchRepository {

    private final FailedSearchJpaRepository jpa;

    public FailedSearchRepositoryAdapter(FailedSearchJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<FailedSearchRecord> findByEventId(String eventId) {
        return jpa.findById(eventId).map(this::toDomain);
    }

    @Override
    public void save(FailedSearchRecord record) {
        jpa.save(new FailedSearchJpaEntity(
                record.eventId(),
                record.partQuery().value(),
                record.workshopId().value(),
                record.searchedAt()
        ));
    }

    @Override
    public void deleteAll() {
        jpa.deleteAll();
    }

    @Override
    public List<TrendSummary> topFailedQueries(int limit) {
        return jpa.aggregateTrends(PageRequest.of(0, limit)).stream()
                .map(r -> new TrendSummary(new PartQuery(r.getPartQuery()),
                        r.getFailCount(), r.getFirstSeen(), r.getLastSeen()))
                .toList();
    }

    private FailedSearchRecord toDomain(FailedSearchJpaEntity e) {
        return new FailedSearchRecord(
                e.getEventId(),
                new PartQuery(e.getPartQuery()),
                new WorkshopId(e.getWorkshopId()),
                e.getSearchedAt()
        );
    }
}
