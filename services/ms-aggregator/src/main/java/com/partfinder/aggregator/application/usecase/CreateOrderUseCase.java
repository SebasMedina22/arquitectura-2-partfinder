package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.exception.WorkshopNotFoundException;
import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Order;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.Quantity;
import com.partfinder.aggregator.domain.model.SupplierId;
import com.partfinder.aggregator.domain.model.Workshop;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.policy.OrderRulePolicy;
import com.partfinder.aggregator.domain.port.out.OrderRepository;
import com.partfinder.aggregator.domain.port.out.WorkshopRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

/**
 * Caso de uso: crear un pedido. Aplica las OrderRulePolicy (Strategy GoF):
 * hoy solo OrderCreditPolicy (R2). Si pasa, persiste la orden y descuenta el cupo.
 */
public class CreateOrderUseCase {

    private final List<OrderRulePolicy> policies;
    private final WorkshopRepository workshops;
    private final OrderRepository orders;
    private final Clock clock;

    public CreateOrderUseCase(List<OrderRulePolicy> policies,
                              WorkshopRepository workshops,
                              OrderRepository orders,
                              Clock clock) {
        this.policies = policies;
        this.workshops = workshops;
        this.orders = orders;
        this.clock = clock;
    }

    public Order execute(WorkshopId workshopId, PartId partId, SupplierId supplierId,
                         Quantity quantity, Money unitPrice) {
        Workshop workshop = workshops.findById(workshopId)
                .orElseThrow(() -> new WorkshopNotFoundException("Workshop " + workshopId + " no encontrado"));

        Money total = new Money(unitPrice.amount().multiply(java.math.BigDecimal.valueOf(quantity.value())),
                unitPrice.currency());

        for (OrderRulePolicy policy : policies) {
            policy.check(workshop, total);
        }

        Order order = Order.createNew(workshopId, partId, supplierId, quantity, total, Instant.now(clock));
        workshop.chargeAdditional(total);

        orders.save(order);
        workshops.save(workshop);
        return order;
    }
}
