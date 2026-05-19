package com.partfinder.aggregator.infrastructure.persistence;

import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Order;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.Quantity;
import com.partfinder.aggregator.domain.model.SupplierId;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.port.out.OrderRepository;
import com.partfinder.aggregator.infrastructure.persistence.jpa.OrderJpaEntity;
import com.partfinder.aggregator.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpa;

    public OrderRepositoryAdapter(OrderJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Order> findById(String id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Order> findByWorkshop(WorkshopId workshopId) {
        return jpa.findByWorkshopId(workshopId.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public void save(Order order) {
        jpa.save(new OrderJpaEntity(
                order.id(), order.workshopId().value(),
                order.partId().value(), order.supplierId().value(),
                order.quantity().value(),
                order.totalAmount().amount(), order.totalAmount().currency(),
                order.status(), order.createdAt()
        ));
    }

    private Order toDomain(OrderJpaEntity e) {
        return new Order(
                e.getId(),
                new WorkshopId(e.getWorkshopId()),
                new PartId(e.getPartId()),
                new SupplierId(e.getSupplierId()),
                Quantity.of(e.getQuantity()),
                new Money(e.getTotalAmount(), e.getTotalCurrency()),
                e.getCreatedAt(),
                e.getStatus()
        );
    }
}
