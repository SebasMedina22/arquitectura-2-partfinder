package com.partfinder.aggregator.infrastructure.client;

import com.partfinder.aggregator.domain.model.InventoryQueryResult;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.SupplierId;
import com.partfinder.aggregator.domain.port.out.InventoryDirectPort;
import feign.FeignException;
import feign.RetryableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Patron Adapter (GoF). Traduce el puerto del dominio (InventoryDirectPort) a
 * una llamada Feign concreta.
 *
 * Aqui se MATERIALIZA la Regla R1 del caso 7:
 *   - Feign read-timeout = 800ms (config en application.yml)
 *   - Si la llamada tarda mas de 800ms, Feign lanza RetryableException con
 *     SocketTimeoutException dentro.
 *   - Capturamos esa excepcion y devolvemos un InventoryQueryResult con
 *     timedOut=true; el caso de uso lo traduce a Availability.UNCERTAIN.
 *
 * Adicionalmente envuelto con Resilience4j Circuit Breaker — si InventoryDirect
 * sigue degradando (>50% de fallos o >80% de llamadas lentas), abrimos el breaker
 * para no martillarlo y entrar al fallback con UNCERTAIN tambien.
 */
@Component
public class InventoryDirectFeignAdapter implements InventoryDirectPort {

    private static final Logger log = LoggerFactory.getLogger(InventoryDirectFeignAdapter.class);

    private final InventoryDirectFeignClient client;

    public InventoryDirectFeignAdapter(InventoryDirectFeignClient client) {
        this.client = client;
    }

    @Override
    @CircuitBreaker(name = "inventory", fallbackMethod = "markUncertain")
    public InventoryQueryResult fetchInventoryFor(PartId partId) {
        try {
            List<InventoryItemDto> items = client.getInventory(partId.value());
            List<InventoryQueryResult.Offer> offers = items.stream()
                    .map(it -> new InventoryQueryResult.Offer(new SupplierId(it.supplierId()), it.stock()))
                    .toList();
            return new InventoryQueryResult(offers, false);
        } catch (RetryableException e) {
            // Socket timeout — InventoryDirect tardo mas de 800ms. R1 dispara.
            log.warn("Timeout en InventoryDirect para {} ({}ms): R1 marca UNCERTAIN",
                    partId, e.getMessage());
            return new InventoryQueryResult(List.of(), true);
        } catch (FeignException.NotFound e) {
            // El MS sincrono dice que esa parte no existe. No es timeout; el caso de uso
            // ya lo manejara via catalogo + offers vacios -> puede ser NOT_FOUND.
            return new InventoryQueryResult(List.of(), false);
        } catch (FeignException e) {
            // Otros errores HTTP (5xx, 4xx no esperados): degradamos a UNCERTAIN
            // para no romper la busqueda del usuario.
            log.warn("Error HTTP {} en InventoryDirect para {}: aplicando UNCERTAIN", e.status(), partId);
            return new InventoryQueryResult(List.of(), true);
        }
    }

    @SuppressWarnings("unused")
    private InventoryQueryResult markUncertain(PartId partId, Throwable t) {
        log.warn("Fallback de CircuitBreaker para {} ({}): UNCERTAIN",
                partId, t.getClass().getSimpleName());
        return new InventoryQueryResult(List.of(), true);
    }
}
