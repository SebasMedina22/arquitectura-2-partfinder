package com.partfinder.aggregator.infrastructure.rest;

import com.partfinder.aggregator.application.usecase.ResetDemoUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Tag(name = "Demo admin", description = "Utilidades para reiniciar el estado de demo.")
public class DemoAdminController {

    private final ResetDemoUseCase resetDemo;

    public DemoAdminController(ResetDemoUseCase resetDemo) {
        this.resetDemo = resetDemo;
    }

    @Operation(summary = "Borra todos los pedidos y restaura el cupo sembrado de los talleres.")
    @PostMapping("/reset-demo")
    @Transactional
    public Map<String, Object> reset() {
        resetDemo.execute();
        return Map.of("status", "ok", "message", "Pedidos borrados y cupos restaurados al estado inicial.");
    }
}
