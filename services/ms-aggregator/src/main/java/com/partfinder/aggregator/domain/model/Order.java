package com.partfinder.aggregator.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad: una orden de compra emitida por un taller hacia un proveedor.
 */
public class Order {

    private final String id;
    private final WorkshopId workshopId;
    private final PartId partId;
    private final SupplierId supplierId;
    private final Quantity quantity;
    private final Money totalAmount;
    private final Instant createdAt;
    private OrderStatus status;

    public Order(String id, WorkshopId workshopId, PartId partId, SupplierId supplierId,
                 Quantity quantity, Money totalAmount, Instant createdAt, OrderStatus status) {
        this.id = Objects.requireNonNull(id);
        this.workshopId = Objects.requireNonNull(workshopId);
        this.partId = Objects.requireNonNull(partId);
        this.supplierId = Objects.requireNonNull(supplierId);
        this.quantity = Objects.requireNonNull(quantity);
        this.totalAmount = Objects.requireNonNull(totalAmount);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.status = Objects.requireNonNull(status);
    }

    public static Order createNew(WorkshopId workshopId, PartId partId, SupplierId supplierId,
                                  Quantity quantity, Money totalAmount, Instant at) {
        return new Order(UUID.randomUUID().toString(), workshopId, partId, supplierId,
                quantity, totalAmount, at, OrderStatus.CREATED);
    }

    public String id() { return id; }
    public WorkshopId workshopId() { return workshopId; }
    public PartId partId() { return partId; }
    public SupplierId supplierId() { return supplierId; }
    public Quantity quantity() { return quantity; }
    public Money totalAmount() { return totalAmount; }
    public Instant createdAt() { return createdAt; }
    public OrderStatus status() { return status; }
}
