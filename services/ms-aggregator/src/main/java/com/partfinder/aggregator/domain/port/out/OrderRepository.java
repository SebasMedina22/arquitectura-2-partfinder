package com.partfinder.aggregator.domain.port.out;

import com.partfinder.aggregator.domain.model.Order;
import com.partfinder.aggregator.domain.model.SupplierId;
import com.partfinder.aggregator.domain.model.WorkshopId;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Optional<Order> findById(String id);
    List<Order> findByWorkshop(WorkshopId workshopId);
    List<Order> findBySupplier(SupplierId supplierId);
    void save(Order order);
    void deleteAll();
}
