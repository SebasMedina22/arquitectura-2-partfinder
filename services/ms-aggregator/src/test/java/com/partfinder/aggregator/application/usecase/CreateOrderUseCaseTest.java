package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.exception.OrderRuleViolationException;
import com.partfinder.aggregator.domain.exception.WorkshopNotFoundException;
import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Order;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.Quantity;
import com.partfinder.aggregator.domain.model.SupplierId;
import com.partfinder.aggregator.domain.model.Workshop;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.policy.OrderCreditPolicy;
import com.partfinder.aggregator.domain.policy.OrderRulePolicy;
import com.partfinder.aggregator.domain.port.out.OrderRepository;
import com.partfinder.aggregator.domain.port.out.WorkshopRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderUseCaseTest {

    private final Clock fixed = Clock.fixed(Instant.parse("2026-05-19T10:00:00Z"), ZoneId.of("UTC"));

    private CreateOrderUseCase build(Map<String, Workshop> wsStore, Map<String, Order> orderStore) {
        WorkshopRepository workshops = new WorkshopRepository() {
            @Override public Optional<Workshop> findById(WorkshopId id) { return Optional.ofNullable(wsStore.get(id.value())); }
            @Override public void save(Workshop w) { wsStore.put(w.id().value(), w); }
        };
        OrderRepository orders = new OrderRepository() {
            @Override public Optional<Order> findById(String id) { return Optional.ofNullable(orderStore.get(id)); }
            @Override public List<Order> findByWorkshop(WorkshopId w) { return List.of(); }
            @Override public List<Order> findBySupplier(com.partfinder.aggregator.domain.model.SupplierId s) { return List.of(); }
            @Override public void save(Order o) { orderStore.put(o.id(), o); }
            @Override public void deleteAll() { orderStore.clear(); }
        };
        List<OrderRulePolicy> policies = List.of(new OrderCreditPolicy());
        return new CreateOrderUseCase(policies, workshops, orders, fixed);
    }

    @Test
    void creaOrden_yDescuentaCredito() {
        Map<String, Workshop> ws = new HashMap<>();
        ws.put("WS-001", new Workshop(new WorkshopId("WS-001"), "OK",
                Money.cop(new BigDecimal("1000000")), Money.cop(new BigDecimal("100000"))));
        Map<String, Order> orders = new HashMap<>();
        CreateOrderUseCase uc = build(ws, orders);

        Order o = uc.execute(new WorkshopId("WS-001"), new PartId("PRT-FO-001"),
                new SupplierId("SUP-LIMA"), Quantity.of(2), Money.cop(new BigDecimal("35000")));

        assertEquals(0, o.totalAmount().amount().compareTo(new BigDecimal("70000")));
        assertEquals(0, ws.get("WS-001").creditUsed().amount().compareTo(new BigDecimal("170000")));
    }

    @Test
    void rechaza_porR2_cuandoYaEstaExcedido() {
        Map<String, Workshop> ws = new HashMap<>();
        ws.put("WS-003", new Workshop(new WorkshopId("WS-003"), "Excedido",
                Money.cop(new BigDecimal("1000000")), Money.cop(new BigDecimal("1200000"))));
        CreateOrderUseCase uc = build(ws, new HashMap<>());

        OrderRuleViolationException ex = assertThrows(OrderRuleViolationException.class,
                () -> uc.execute(new WorkshopId("WS-003"), new PartId("PRT-FO-001"),
                        new SupplierId("SUP-LIMA"), Quantity.of(1), Money.cop(new BigDecimal("35000"))));
        assertEquals("R2_CREDIT_EXCEEDED", ex.ruleCode());
    }

    @Test
    void faltaWorkshop_lanzaWorkshopNotFound() {
        CreateOrderUseCase uc = build(new HashMap<>(), new HashMap<>());
        assertThrows(WorkshopNotFoundException.class,
                () -> uc.execute(new WorkshopId("WS-999"), new PartId("PRT-FO-001"),
                        new SupplierId("SUP-LIMA"), Quantity.of(1), Money.cop(new BigDecimal("10000"))));
    }
}
