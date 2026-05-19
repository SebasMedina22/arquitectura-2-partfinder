package com.partfinder.aggregator.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI aggregatorOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("PartFinder - MS-Aggregator")
                .description("Microservicio principal hexagonal. Orquesta R1 (timeout 800ms -> UNCERTAIN), " +
                        "R2 (cupo de credito) y R3 (busquedas fallidas -> Outbox -> TrendCollector).")
                .version("0.1.0"));
    }
}
