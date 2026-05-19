package com.partfinder.inventory.infrastructure.rest;

import com.partfinder.inventory.application.service.SlowModeService;
import com.partfinder.inventory.application.usecase.GetInventoryByPartUseCase;
import com.partfinder.inventory.application.usecase.UpsertInventoryUseCase;
import com.partfinder.inventory.domain.model.PartId;
import com.partfinder.inventory.domain.model.StockQuantity;
import com.partfinder.inventory.domain.model.SupplierId;
import com.partfinder.inventory.infrastructure.rest.dto.InventoryItemResponse;
import com.partfinder.inventory.infrastructure.rest.dto.SlowModeRequest;
import com.partfinder.inventory.infrastructure.rest.dto.UpsertInventoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@Tag(name = "Inventory", description = "Inventario por (parte, proveedor). Soporta slow-mode para demos de R1.")
public class InventoryController {

    private final GetInventoryByPartUseCase getInventory;
    private final UpsertInventoryUseCase upsertInventory;
    private final SlowModeService slowMode;

    public InventoryController(GetInventoryByPartUseCase getInventory,
                               UpsertInventoryUseCase upsertInventory,
                               SlowModeService slowMode) {
        this.getInventory = getInventory;
        this.upsertInventory = upsertInventory;
        this.slowMode = slowMode;
    }

    @Operation(summary = "Lista la disponibilidad de un repuesto en todos los proveedores. Aplica slow-mode si esta activo (para forzar R1).")
    @GetMapping("/inventory")
    public List<InventoryItemResponse> getInventory(@RequestParam("partId") String partId) {
        slowMode.applyDelayIfAny();
        return getInventory.execute(new PartId(partId)).stream().map(InventoryItemResponse::from).toList();
    }

    @Operation(summary = "Admin: crea o actualiza el stock de (parte, proveedor).")
    @PostMapping("/inventory")
    public ResponseEntity<InventoryItemResponse> upsert(@Valid @RequestBody UpsertInventoryRequest req) {
        var item = upsertInventory.execute(
                new PartId(req.partId()),
                new SupplierId(req.supplierId()),
                StockQuantity.of(req.stock())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(InventoryItemResponse.from(item));
    }

    @Operation(summary = "Admin: setea un delay (ms) que se aplica en cada GET /inventory. Util para forzar timeout > 800ms en el Aggregator y disparar R1 (UNCERTAIN).")
    @PostMapping("/admin/slow-mode")
    public Map<String, Long> setSlowMode(@Valid @RequestBody SlowModeRequest req) {
        slowMode.setDelayMs(req.delayMs());
        return Map.of("delayMs", slowMode.getDelayMs());
    }

    @Operation(summary = "Admin: consulta el delay actual.")
    @GetMapping("/admin/slow-mode")
    public Map<String, Long> getSlowMode() {
        return Map.of("delayMs", slowMode.getDelayMs());
    }
}
