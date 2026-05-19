package com.partfinder.aggregator.domain.factory;

import com.partfinder.aggregator.domain.model.Availability;
import com.partfinder.aggregator.domain.model.InventoryQueryResult;
import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Part;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.SearchResult;
import com.partfinder.aggregator.domain.model.SupplierId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SearchResultFactoryTest {

    private final SearchResultFactory factory = new SearchResultFactory();
    private final PartId partId = new PartId("PRT-FO-001");

    @Test
    void timeout_marcaUncertain_aunqueHayaStock() {
        Part part = new Part(partId, "Filtro", "desc", "filtros", Money.cop(new BigDecimal("35000")));
        InventoryQueryResult inv = new InventoryQueryResult(
                List.of(new InventoryQueryResult.Offer(new SupplierId("SUP-LIMA"), 5)),
                true /* timedOut */);
        SearchResult sr = factory.build(partId, Optional.of(part), inv);
        assertEquals(Availability.UNCERTAIN, sr.availability());
        assertEquals(1, sr.supplierOffers().size());
    }

    @Test
    void sinCatalogo_yStockVacio_devuelveNotFound() {
        InventoryQueryResult inv = new InventoryQueryResult(List.of(), false);
        SearchResult sr = factory.build(partId, Optional.empty(), inv);
        assertEquals(Availability.NOT_FOUND, sr.availability());
    }

    @Test
    void conCatalogo_yStock_devuelveAvailable() {
        Part part = new Part(partId, "Filtro", "desc", "filtros", Money.cop(new BigDecimal("35000")));
        InventoryQueryResult inv = new InventoryQueryResult(
                List.of(new InventoryQueryResult.Offer(new SupplierId("SUP-LIMA"), 5)),
                false);
        SearchResult sr = factory.build(partId, Optional.of(part), inv);
        assertEquals(Availability.AVAILABLE, sr.availability());
    }

    @Test
    void conCatalogo_sinStock_devuelveNotFound() {
        Part part = new Part(partId, "Embrague", "desc", "transmision", Money.cop(new BigDecimal("1200000")));
        InventoryQueryResult inv = new InventoryQueryResult(
                List.of(new InventoryQueryResult.Offer(new SupplierId("SUP-LIMA"), 0),
                        new InventoryQueryResult.Offer(new SupplierId("SUP-MEDLN"), 0)),
                false);
        SearchResult sr = factory.build(partId, Optional.of(part), inv);
        assertEquals(Availability.NOT_FOUND, sr.availability());
    }
}
