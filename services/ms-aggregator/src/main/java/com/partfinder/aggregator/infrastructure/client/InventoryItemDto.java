package com.partfinder.aggregator.infrastructure.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de respuesta del endpoint GET /inventory de MS-InventoryDirect.
 * ignoreUnknown = true porque Feign no hereda la configuracion Jackson de Spring por defecto
 * (leccion aprendida en VoltNet).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InventoryItemDto(
        @JsonProperty("partId") String partId,
        @JsonProperty("supplierId") String supplierId,
        @JsonProperty("stock") int stock,
        @JsonProperty("available") boolean available
) {
}
