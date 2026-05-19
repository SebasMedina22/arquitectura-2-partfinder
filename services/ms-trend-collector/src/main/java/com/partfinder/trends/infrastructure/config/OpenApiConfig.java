package com.partfinder.trends.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI trendsOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("PartFinder - MS-TrendCollector")
                .description("Consumer asincrono de SearchFailedEvent. Acumula tendencias por query y las expone para analisis.")
                .version("0.1.0"));
    }
}
