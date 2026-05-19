package com.partfinder.trends.infrastructure.rest.dto;

import com.partfinder.trends.domain.model.TrendSummary;

import java.time.Instant;

public record TrendResponse(
        String partQuery,
        long failCount,
        Instant firstSeen,
        Instant lastSeen
) {
    public static TrendResponse from(TrendSummary s) {
        return new TrendResponse(s.partQuery().value(), s.failCount(), s.firstSeen(), s.lastSeen());
    }
}
