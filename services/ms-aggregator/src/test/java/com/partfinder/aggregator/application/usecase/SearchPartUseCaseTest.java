package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.event.SearchFailedEvent;
import com.partfinder.aggregator.domain.factory.SearchResultFactory;
import com.partfinder.aggregator.domain.model.Availability;
import com.partfinder.aggregator.domain.model.InventoryQueryResult;
import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Part;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.SearchResult;
import com.partfinder.aggregator.domain.model.SupplierId;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.port.out.DomainEventPublisher;
import com.partfinder.aggregator.domain.port.out.InventoryDirectPort;
import com.partfinder.aggregator.domain.port.out.PartCatalogRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SearchPartUseCaseTest {

    private final Clock fixed = Clock.fixed(Instant.parse("2026-05-19T10:00:00Z"), ZoneId.of("UTC"));

    @Test
    void caminoFeliz_devuelveAvailable_yNoPublicaEvento() {
        Part part = new Part(new PartId("PRT-FO-001"), "Filtro", "d", "filtros",
                Money.cop(new BigDecimal("35000")));
        PartCatalogRepository catalog = catalogConOnePart(part);
        InventoryDirectPort inventory = id -> new InventoryQueryResult(
                List.of(new InventoryQueryResult.Offer(new SupplierId("SUP-LIMA"), 5)), false);
        List<SearchFailedEvent> published = new ArrayList<>();
        DomainEventPublisher publisher = published::add;

        SearchPartUseCase uc = new SearchPartUseCase(catalog, inventory, new SearchResultFactory(),
                publisher, fixed, 10);
        List<SearchResult> results = uc.execute("filtro", new WorkshopId("WS-001"));

        assertEquals(1, results.size());
        assertEquals(Availability.AVAILABLE, results.get(0).availability());
        assertEquals(0, published.size(), "no debe publicarse SearchFailedEvent en caso feliz");
    }

    @Test
    void timeoutDeInventario_marcaUncertain_yNoPublica() {
        Part part = new Part(new PartId("PRT-FO-001"), "Filtro", "d", "filtros",
                Money.cop(new BigDecimal("35000")));
        PartCatalogRepository catalog = catalogConOnePart(part);
        InventoryDirectPort inventory = id -> new InventoryQueryResult(List.of(), true /* timedOut */);
        List<SearchFailedEvent> published = new ArrayList<>();
        DomainEventPublisher publisher = published::add;

        SearchPartUseCase uc = new SearchPartUseCase(catalog, inventory, new SearchResultFactory(),
                publisher, fixed, 10);
        List<SearchResult> results = uc.execute("filtro", new WorkshopId("WS-001"));

        assertEquals(Availability.UNCERTAIN, results.get(0).availability());
        assertEquals(0, published.size(), "UNCERTAIN no es NOT_FOUND, no debe publicar evento");
    }

    @Test
    void busquedaSinResultadosEnCatalogo_publicaSearchFailed() {
        PartCatalogRepository catalog = new PartCatalogRepository() {
            @Override public Optional<Part> findById(PartId id) { return Optional.empty(); }
            @Override public List<Part> search(String q, int l) { return List.of(); }
            @Override public void save(Part p) {}
        };
        InventoryDirectPort inventory = id -> new InventoryQueryResult(List.of(), false);
        List<SearchFailedEvent> published = new ArrayList<>();
        DomainEventPublisher publisher = published::add;

        SearchPartUseCase uc = new SearchPartUseCase(catalog, inventory, new SearchResultFactory(),
                publisher, fixed, 10);
        uc.execute("repuesto inexistente", new WorkshopId("WS-001"));

        assertEquals(1, published.size(), "debe haberse publicado SearchFailedEvent");
        assertEquals("repuesto inexistente", published.get(0).partQuery());
        assertEquals("WS-001", published.get(0).workshopId());
    }

    @Test
    void partInCatalogo_perosinStock_marcaNotFound_yPublica() {
        Part part = new Part(new PartId("PRT-EM-099"), "Embrague", "d", "transmision",
                Money.cop(new BigDecimal("1200000")));
        PartCatalogRepository catalog = catalogConOnePart(part);
        InventoryDirectPort inventory = id -> new InventoryQueryResult(
                List.of(new InventoryQueryResult.Offer(new SupplierId("SUP-LIMA"), 0)),
                false);
        List<SearchFailedEvent> published = new ArrayList<>();
        DomainEventPublisher publisher = published::add;

        SearchPartUseCase uc = new SearchPartUseCase(catalog, inventory, new SearchResultFactory(),
                publisher, fixed, 10);
        List<SearchResult> results = uc.execute("embrague", new WorkshopId("WS-001"));

        assertEquals(Availability.NOT_FOUND, results.get(0).availability());
        assertEquals(1, published.size());
    }

    private PartCatalogRepository catalogConOnePart(Part p) {
        return new PartCatalogRepository() {
            @Override public Optional<Part> findById(PartId id) { return id.equals(p.id()) ? Optional.of(p) : Optional.empty(); }
            @Override public List<Part> search(String q, int l) { return List.of(p); }
            @Override public void save(Part part) {}
        };
    }
}
