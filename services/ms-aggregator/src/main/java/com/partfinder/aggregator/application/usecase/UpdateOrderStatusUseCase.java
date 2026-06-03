package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.exception.OrderNotFoundException;
import com.partfinder.aggregator.domain.model.Order;
import com.partfinder.aggregator.domain.port.out.OrderRepository;
import com.partfinder.aggregator.domain.port.out.WorkshopRepository;

/**
 * Transiciones del ciclo de vida del pedido: CREATED -> FULFILLED (entregado)
 * o CREATED -> CANCELLED (cancelado). Al cancelar se devuelve el cupo de credito
 * consumido al crear el pedido. Las transiciones solo aplican desde CREATED.
 */
public class UpdateOrderStatusUseCase {

    private final OrderRepository orders;
    private final WorkshopRepository workshops;

    public UpdateOrderStatusUseCase(OrderRepository orders, WorkshopRepository workshops) {
        this.orders = orders;
        this.workshops = workshops;
    }

    public Order fulfill(String id) {
        Order order = load(id);
        if (order.isCreated()) {
            order.markFulfilled();
            orders.save(order);
        }
        return order;
    }

    public Order cancel(String id) {
        Order order = load(id);
        if (order.isCreated()) {
            order.markCancelled();
            workshops.findById(order.workshopId()).ifPresent(w -> {
                w.release(order.totalAmount()); // devuelve el cupo reservado
                workshops.save(w);
            });
            orders.save(order);
        }
        return order;
    }

    private Order load(String id) {
        return orders.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Pedido " + id + " no encontrado"));
    }
}
