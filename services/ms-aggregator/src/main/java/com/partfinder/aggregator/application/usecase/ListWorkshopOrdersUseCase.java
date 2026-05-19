package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.model.Order;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.port.out.OrderRepository;

import java.util.List;

public class ListWorkshopOrdersUseCase {

    private final OrderRepository orders;

    public ListWorkshopOrdersUseCase(OrderRepository orders) {
        this.orders = orders;
    }

    public List<Order> execute(WorkshopId workshopId) {
        return orders.findByWorkshop(workshopId);
    }
}
