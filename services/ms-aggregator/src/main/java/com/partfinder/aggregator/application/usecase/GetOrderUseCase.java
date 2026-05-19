package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.exception.OrderNotFoundException;
import com.partfinder.aggregator.domain.model.Order;
import com.partfinder.aggregator.domain.port.out.OrderRepository;

public class GetOrderUseCase {

    private final OrderRepository orders;

    public GetOrderUseCase(OrderRepository orders) {
        this.orders = orders;
    }

    public Order execute(String orderId) {
        return orders.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderId + " no encontrado"));
    }
}
