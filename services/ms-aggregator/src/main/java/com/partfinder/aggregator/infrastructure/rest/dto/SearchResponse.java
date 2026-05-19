package com.partfinder.aggregator.infrastructure.rest.dto;

import com.partfinder.aggregator.domain.model.SearchResult;

import java.math.BigDecimal;
import java.util.List;

public record SearchResponse(
        String partId,
        String partName,
        BigDecimal referencePrice,
        String currency,
        String availability,
        List<SupplierOfferDto> offers
) {
    public static SearchResponse from(SearchResult r) {
        return new SearchResponse(
                r.partId().value(),
                r.partName(),
                r.referencePrice().amount(),
                r.referencePrice().currency(),
                r.availability().name(),
                r.supplierOffers().stream()
                        .map(o -> new SupplierOfferDto(o.supplierId().value(), o.stock()))
                        .toList()
        );
    }

    public record SupplierOfferDto(String supplierId, int stock) {}
}
