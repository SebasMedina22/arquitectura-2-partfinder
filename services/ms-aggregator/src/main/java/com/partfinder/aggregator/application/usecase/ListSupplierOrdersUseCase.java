package com.partfinder.aggregator.application.usecase;

import com.partfinder.aggregator.domain.model.Order;
import com.partfinder.aggregator.domain.model.SupplierId;
import com.partfinder.aggregator.domain.port.out.OrderRepository;

import java.util.List;

/** Lista los pedidos dirigidos a un proveedor (vista del rol Proveedor). */
public class ListSupplierOrdersUseCase {

    private final OrderRepository orders;

    public ListSupplierOrdersUseCase(OrderRepository orders) {
        this.orders = orders;
    }

    public List<Order> execute(SupplierId supplierId) {
        return orders.findBySupplier(supplierId);
    }
}
