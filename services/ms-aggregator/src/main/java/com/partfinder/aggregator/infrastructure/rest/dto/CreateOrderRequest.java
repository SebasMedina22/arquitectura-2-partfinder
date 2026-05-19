package com.partfinder.aggregator.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank String workshopId,
        @NotBlank String partId,
        @NotBlank String supplierId,
        @Positive int quantity,
        @NotNull @PositiveOrZero BigDecimal unitPrice,
        @NotBlank String currency
) {}
