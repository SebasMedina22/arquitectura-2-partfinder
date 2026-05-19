package com.partfinder.inventory.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("PartFinder - MS-InventoryDirect")
                .description("Microservicio sincrono que simula las bodegas de repuestos. " +
                        "Acepta admin/slow-mode para inducir timeouts > 800ms y forzar R1 desde el Aggregator.")
                .version("0.1.0"));
    }
}
