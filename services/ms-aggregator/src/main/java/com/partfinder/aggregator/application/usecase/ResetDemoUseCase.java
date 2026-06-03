package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Workshop;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.port.out.OrderRepository;
import com.partfinder.aggregator.domain.port.out.WorkshopRepository;

import java.math.BigDecimal;

/**
 * Reinicia el estado de demo: borra todos los pedidos y restaura el cupo de los
 * talleres a sus valores sembrados (WS-001 sano, WS-002 al 80%, WS-003 excedido).
 * Pensado para dejar todo limpio antes de una sustentacion.
 */
public class ResetDemoUseCase {

    private final OrderRepository orders;
    private final WorkshopRepository workshops;

    public ResetDemoUseCase(OrderRepository orders, WorkshopRepository workshops) {
        this.orders = orders;
        this.workshops = workshops;
    }

    public void execute() {
        orders.deleteAll();
        // Mismos valores que el SeedDataRunner.
        workshops.save(new Workshop(new WorkshopId("WS-001"), "Taller La Esquina",
                Money.cop(new BigDecimal("5000000")), Money.cop(new BigDecimal("500000"))));
        workshops.save(new Workshop(new WorkshopId("WS-002"), "Mecanica San Jose",
                Money.cop(new BigDecimal("3000000")), Money.cop(new BigDecimal("2400000"))));
        workshops.save(new Workshop(new WorkshopId("WS-003"), "Auto Servicios Bolivar",
                Money.cop(new BigDecimal("2000000")), Money.cop(new BigDecimal("2200000"))));
    }
}
