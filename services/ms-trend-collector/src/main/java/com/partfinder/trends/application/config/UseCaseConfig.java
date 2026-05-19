package com.partfinder.trends.application.config;

import com.partfinder.trends.application.usecase.GetTopTrendsUseCase;
import com.partfinder.trends.application.usecase.RecordFailedSearchUseCase;
import com.partfinder.trends.domain.port.out.FailedSearchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RecordFailedSearchUseCase recordFailedSearchUseCase(FailedSearchRepository repo) {
        return new RecordFailedSearchUseCase(repo);
    }

    @Bean
    public GetTopTrendsUseCase getTopTrendsUseCase(FailedSearchRepository repo) {
        return new GetTopTrendsUseCase(repo);
    }
}
