package com.partfinder.aggregator.infrastructure.rest;

import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Workshop;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.port.out.WorkshopRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Endpoints admin/demo para inspeccionar y simular cambios en el cupo de credito
 * (sirve para forzar la demo de R2: ponemos un taller cerca del limite y mostramos
 * como pasa de 201 a 422 al hacer un pedido grande).
 */
@RestController
@RequestMapping("/admin/workshops")
@Tag(name = "Workshops admin", description = "Consultar / ajustar el cupo de credito de un taller (proyeccion local).")
public class WorkshopAdminController {

    private final WorkshopRepository workshops;

    public WorkshopAdminController(WorkshopRepository workshops) {
        this.workshops = workshops;
    }

    @Operation(summary = "Consulta el cupo y deuda actual del taller.")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> get(@PathVariable("id") String id) {
        return workshops.findById(new WorkshopId(id))
                .map(w -> ResponseEntity.ok(Map.<String, Object>of(
                        "id", w.id().value(),
                        "name", w.name(),
                        "creditLimit", w.creditLimit().amount(),
                        "creditUsed", w.creditUsed().amount(),
                        "creditAvailable", w.creditAvailable().amount(),
                        "currency", w.creditLimit().currency(),
                        "exceeded", w.hasExceededCredit())))
                .orElse(ResponseEntity.notFound().build());
    }

    public record SetCreditUsedRequest(@NotNull @PositiveOrZero BigDecimal amount) {}

    @Operation(summary = "Admin: setea el credito usado del taller (para forzar la regla R2 en demos).")
    @PostMapping("/{id}/credit-used")
    public ResponseEntity<Map<String, Object>> setCreditUsed(@PathVariable("id") String id,
                                                             @Valid @RequestBody SetCreditUsedRequest req) {
        return workshops.findById(new WorkshopId(id)).map(w -> {
            Workshop updated = new Workshop(
                    w.id(), w.name(), w.creditLimit(),
                    new Money(req.amount(), w.creditUsed().currency()));
            workshops.save(updated);
            return ResponseEntity.ok(Map.<String, Object>of(
                    "id", updated.id().value(),
                    "creditUsed", updated.creditUsed().amount(),
                    "exceeded", updated.hasExceededCredit()));
        }).orElse(ResponseEntity.notFound().build());
    }

    public record SetCreditLimitRequest(@NotNull @PositiveOrZero BigDecimal amount) {}

    @Operation(summary = "Admin: setea el cupo (tope) de credito del taller.")
    @PostMapping("/{id}/credit-limit")
    public ResponseEntity<Map<String, Object>> setCreditLimit(@PathVariable("id") String id,
                                                              @Valid @RequestBody SetCreditLimitRequest req) {
        return workshops.findById(new WorkshopId(id)).map(w -> {
            Workshop updated = new Workshop(
                    w.id(), w.name(),
                    new Money(req.amount(), w.creditLimit().currency()),
                    w.creditUsed());
            workshops.save(updated);
            return ResponseEntity.ok(Map.<String, Object>of(
                    "id", updated.id().value(),
                    "creditLimit", updated.creditLimit().amount(),
                    "exceeded", updated.hasExceededCredit()));
        }).orElse(ResponseEntity.notFound().build());
    }
}
