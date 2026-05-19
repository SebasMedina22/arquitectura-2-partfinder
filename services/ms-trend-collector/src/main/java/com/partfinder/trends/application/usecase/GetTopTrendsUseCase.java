package com.partfinder.trends.application.usecase;

import com.partfinder.trends.domain.model.TrendSummary;
import com.partfinder.trends.domain.port.out.FailedSearchRepository;

import java.util.List;

public class GetTopTrendsUseCase {

    private final FailedSearchRepository repository;

    public GetTopTrendsUseCase(FailedSearchRepository repository) {
        this.repository = repository;
    }

    public List<TrendSummary> execute(int limit) {
        return repository.topFailedQueries(Math.max(1, Math.min(limit, 100)));
    }
}
