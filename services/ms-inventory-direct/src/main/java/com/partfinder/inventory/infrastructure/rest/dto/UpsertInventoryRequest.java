package com.partfinder.inventory.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record UpsertInventoryRequest(
        @NotBlank String partId,
        @NotBlank String supplierId,
        @PositiveOrZero int stock
) {
}
