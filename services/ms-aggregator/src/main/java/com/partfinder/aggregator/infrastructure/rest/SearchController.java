package com.partfinder.aggregator.infrastructure.rest;

import com.partfinder.aggregator.application.usecase.SearchPartUseCase;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.infrastructure.rest.dto.SearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@Tag(name = "Search", description = "Busqueda de repuestos en el catalogo (R1 marca UNCERTAIN ante timeout, R3 publica busquedas fallidas).")
public class SearchController {

    private final SearchPartUseCase searchPart;

    public SearchController(SearchPartUseCase searchPart) {
        this.searchPart = searchPart;
    }

    @Operation(summary = "Busqueda full-text. Aplica R1 (timeout 800ms en InventoryDirect -> UNCERTAIN) y R3 (NOT_FOUND -> evento al outbox).")
    @GetMapping
    @Transactional
    public List<SearchResponse> search(
            @RequestParam("query") String query,
            @RequestParam(value = "workshopId", required = false) String workshopId
    ) {
        WorkshopId wsId = workshopId != null && !workshopId.isBlank() ? new WorkshopId(workshopId) : null;
        return searchPart.execute(query, wsId).stream().map(SearchResponse::from).toList();
    }

    @Operation(summary = "Consulta una parte por id directo. Mismo comportamiento R1/R3.")
    @GetMapping("/by-id")
    @Transactional
    public SearchResponse byId(
            @RequestParam("partId") String partId,
            @RequestParam(value = "workshopId", required = false) String workshopId
    ) {
        WorkshopId wsId = workshopId != null && !workshopId.isBlank() ? new WorkshopId(workshopId) : null;
        return SearchResponse.from(searchPart.executeById(new PartId(partId), wsId));
    }
}
