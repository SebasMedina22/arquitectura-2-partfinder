package com.partfinder.aggregator.infrastructure.rest.dto;

import com.partfinder.aggregator.domain.model.Order;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(
        String id,
        String workshopId,
        String partId,
        String supplierId,
        int quantity,
        BigDecimal totalAmount,
        String currency,
        String status,
        Instant createdAt
) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.id(),
                o.workshopId().value(),
                o.partId().value(),
                o.supplierId().value(),
                o.quantity().value(),
                o.totalAmount().amount(),
                o.totalAmount().currency(),
                o.status().name(),
                o.createdAt()
        );
    }
}
