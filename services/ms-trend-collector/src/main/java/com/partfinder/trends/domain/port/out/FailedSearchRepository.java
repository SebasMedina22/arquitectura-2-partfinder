package com.partfinder.trends.domain.port.out;

import com.partfinder.trends.domain.model.FailedSearchRecord;
import com.partfinder.trends.domain.model.TrendSummary;

import java.util.List;
import java.util.Optional;

public interface FailedSearchRepository {

    /** Devuelve el registro si ya existe (idempotencia). */
    Optional<FailedSearchRecord> findByEventId(String eventId);

    void save(FailedSearchRecord record);

    /** Top N queries con mas fallos. */
    List<TrendSummary> topFailedQueries(int limit);

    /** Borra todas las tendencias (utilidad de demo). */
    void deleteAll();
}
