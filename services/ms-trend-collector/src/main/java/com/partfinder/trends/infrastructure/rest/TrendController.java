package com.partfinder.trends.infrastructure.rest;

import com.partfinder.trends.application.usecase.GetTopTrendsUseCase;
import com.partfinder.trends.domain.port.out.FailedSearchRepository;
import com.partfinder.trends.infrastructure.rest.dto.TrendResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trends")
@Tag(name = "Trends", description = "Tendencias de busquedas fallidas (consulta interna, demostrable en Swagger).")
public class TrendController {

    private final GetTopTrendsUseCase getTopTrends;
    private final FailedSearchRepository repository;

    public TrendController(GetTopTrendsUseCase getTopTrends, FailedSearchRepository repository) {
        this.getTopTrends = getTopTrends;
        this.repository = repository;
    }

    @Operation(summary = "Top N partes mas buscadas que ningun proveedor tenia. Insumo para el panel de tendencias.")
    @GetMapping("/top")
    public List<TrendResponse> top(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        return getTopTrends.execute(limit).stream().map(TrendResponse::from).toList();
    }

    @Operation(summary = "Borra todas las tendencias acumuladas (utilidad de demo).")
    @PostMapping("/reset")
    public Map<String, Object> reset() {
        repository.deleteAll();
        return Map.of("status", "ok");
    }
}
