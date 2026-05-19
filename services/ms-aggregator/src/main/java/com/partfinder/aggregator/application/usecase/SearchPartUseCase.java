package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.event.SearchFailedEvent;
import com.partfinder.aggregator.domain.factory.SearchResultFactory;
import com.partfinder.aggregator.domain.model.Availability;
import com.partfinder.aggregator.domain.model.InventoryQueryResult;
import com.partfinder.aggregator.domain.model.Part;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.SearchResult;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.port.out.DomainEventPublisher;
import com.partfinder.aggregator.domain.port.out.InventoryDirectPort;
import com.partfinder.aggregator.domain.port.out.PartCatalogRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Caso de uso: buscar repuestos.
 *
 * Flujo:
 *  1) Busqueda en catalogo de partes (Elasticsearch) por texto libre.
 *  2) Por cada parte candidata, consulta sincrona a InventoryDirect (Feign con read-timeout 800ms).
 *  3) Si la llamada al sincrono tardo > 800ms: el resultado se marca como UNCERTAIN (R1).
 *  4) Si el resultado es NOT_FOUND: se publica SearchFailedEvent al outbox (R3).
 *
 * Orquesta R1 y R3. R2 NO aplica aqui (es del CreateOrderUseCase).
 */
public class SearchPartUseCase {

    private final PartCatalogRepository catalog;
    private final InventoryDirectPort inventory;
    private final SearchResultFactory factory;
    private final DomainEventPublisher eventPublisher;
    private final Clock clock;
    private final int maxResults;

    public SearchPartUseCase(PartCatalogRepository catalog,
                             InventoryDirectPort inventory,
                             SearchResultFactory factory,
                             DomainEventPublisher eventPublisher,
                             Clock clock,
                             int maxResults) {
        this.catalog = catalog;
        this.inventory = inventory;
        this.factory = factory;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
        this.maxResults = maxResults;
    }

    public List<SearchResult> execute(String query, WorkshopId workshopId) {
        List<Part> candidates = catalog.search(query, maxResults);
        List<SearchResult> results = new ArrayList<>();

        if (candidates.isEmpty()) {
            // El usuario busco algo que ni siquiera esta en el catalogo: notificamos a TrendCollector
            publishSearchFailed(query, workshopId);
            return List.of();
        }

        for (Part part : candidates) {
            InventoryQueryResult invResult = inventory.fetchInventoryFor(part.id());
            SearchResult sr = factory.build(part.id(), Optional.of(part), invResult);
            results.add(sr);

            // Si para esta parte ni catalogo ni stock dieron resultado: R3
            if (sr.availability() == Availability.NOT_FOUND) {
                publishSearchFailed(part.id().value(), workshopId);
            }
        }
        return results;
    }

    /**
     * Sobrecarga util para cuando se busca por PartId directamente (no por texto).
     */
    public SearchResult executeById(PartId partId, WorkshopId workshopId) {
        Optional<Part> part = catalog.findById(partId);
        InventoryQueryResult invResult = inventory.fetchInventoryFor(partId);
        SearchResult sr = factory.build(partId, part, invResult);
        if (sr.availability() == Availability.NOT_FOUND) {
            publishSearchFailed(partId.value(), workshopId);
        }
        return sr;
    }

    private void publishSearchFailed(String partQuery, WorkshopId workshopId) {
        SearchFailedEvent ev = new SearchFailedEvent(
                UUID.randomUUID().toString(),
                partQuery,
                workshopId != null ? workshopId.value() : "WS-ANONYMOUS",
                Instant.now(clock)
        );
        eventPublisher.publish(ev);
    }
}
