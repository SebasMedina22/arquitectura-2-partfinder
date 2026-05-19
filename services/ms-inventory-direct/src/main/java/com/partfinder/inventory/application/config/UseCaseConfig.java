package com.partfinder.inventory.application.config;

import com.partfinder.inventory.application.usecase.GetInventoryByPartUseCase;
import com.partfinder.inventory.application.usecase.UpsertInventoryUseCase;
import com.partfinder.inventory.domain.port.out.InventoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Cableado de la capa de aplicacion. El dominio sigue siendo agnostico a Spring;
 * solo aqui se conectan las instancias.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public GetInventoryByPartUseCase getInventoryByPartUseCase(InventoryRepository repo) {
        return new GetInventoryByPartUseCase(repo);
    }

    @Bean
    public UpsertInventoryUseCase upsertInventoryUseCase(InventoryRepository repo, Clock clock) {
        return new UpsertInventoryUseCase(repo, clock);
    }
}
