package com.partfinder.trends.application.usecase;

import com.partfinder.trends.domain.event.SearchFailedEvent;
import com.partfinder.trends.domain.model.FailedSearchRecord;
import com.partfinder.trends.domain.model.TrendSummary;
import com.partfinder.trends.domain.port.out.FailedSearchRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RecordFailedSearchUseCaseTest {

    private final Map<String, FailedSearchRecord> store = new HashMap<>();

    private final FailedSearchRepository repo = new FailedSearchRepository() {
        @Override public Optional<FailedSearchRecord> findByEventId(String eventId) {
            return Optional.ofNullable(store.get(eventId));
        }
        @Override public void save(FailedSearchRecord r) {
            store.put(r.eventId(), r);
        }
        @Override public List<TrendSummary> topFailedQueries(int limit) {
            return List.of();
        }
        @Override public void deleteAll() { store.clear(); }
    };

    @Test
    void persiste_evento_nuevo() {
        var uc = new RecordFailedSearchUseCase(repo);
        var ev = new SearchFailedEvent("evt-1", "Filtro aceite", "WS-001", Instant.parse("2026-05-19T10:00:00Z"));
        uc.execute(ev);
        assertEquals(1, store.size());
        assertEquals("filtro aceite", store.get("evt-1").partQuery().value());
    }

    @Test
    void ignora_evento_duplicado_mismo_eventId() {
        var uc = new RecordFailedSearchUseCase(repo);
        var ev1 = new SearchFailedEvent("evt-1", "Filtro", "WS-001", Instant.parse("2026-05-19T10:00:00Z"));
        var ev2 = new SearchFailedEvent("evt-1", "Filtro otra cosa", "WS-001", Instant.parse("2026-05-19T11:00:00Z"));
        uc.execute(ev1);
        uc.execute(ev2);
        assertEquals(1, store.size());
        assertEquals("filtro", store.get("evt-1").partQuery().value(), "el primer registro NO se sobreescribe");
    }
}
