package com.partfinder.aggregator.infrastructure.persistence.jpa;

import com.partfinder.aggregator.domain.model.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class OrderJpaEntity {

    @Id
    @Column(name = "id", length = 64, columnDefinition = "VARCHAR(64)")
    private String id;

    @Column(name = "workshop_id", length = 40, nullable = false, columnDefinition = "VARCHAR(40)")
    private String workshopId;

    @Column(name = "part_id", length = 40, nullable = false, columnDefinition = "VARCHAR(40)")
    private String partId;

    @Column(name = "supplier_id", length = 40, nullable = false, columnDefinition = "VARCHAR(40)")
    private String supplierId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_currency", length = 3, nullable = false, columnDefinition = "VARCHAR(3)")
    private String totalCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 16, nullable = false, columnDefinition = "VARCHAR(16)")
    private OrderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected OrderJpaEntity() {}

    public OrderJpaEntity(String id, String workshopId, String partId, String supplierId,
                          int quantity, BigDecimal totalAmount, String totalCurrency,
                          OrderStatus status, Instant createdAt) {
        this.id = id; this.workshopId = workshopId; this.partId = partId; this.supplierId = supplierId;
        this.quantity = quantity; this.totalAmount = totalAmount; this.totalCurrency = totalCurrency;
        this.status = status; this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getWorkshopId() { return workshopId; }
    public String getPartId() { return partId; }
    public String getSupplierId() { return supplierId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getTotalCurrency() { return totalCurrency; }
    public OrderStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
