package com.partfinder.inventory.infrastructure.rest.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record SlowModeRequest(@PositiveOrZero long delayMs) {
}
