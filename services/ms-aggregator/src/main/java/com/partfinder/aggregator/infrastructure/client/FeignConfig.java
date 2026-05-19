package com.partfinder.aggregator.infrastructure.client;

import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuracion global de Feign. Define Request.Options con timeouts explicitos
 * para que la R1 del caso (read-timeout=800ms) este garantizada
 * independientemente de cambios en namespaces de propiedades entre versiones
 * de Spring Cloud OpenFeign.
 */
@Configuration
public class FeignConfig {

    @Bean
    public Request.Options feignRequestOptions(
            @Value("${aggregator-feign.connect-timeout-ms:500}") long connectMs,
            @Value("${aggregator-feign.read-timeout-ms:800}") long readMs) {
        return new Request.Options(
                connectMs, TimeUnit.MILLISECONDS,
                readMs, TimeUnit.MILLISECONDS,
                true /* followRedirects */
        );
    }
}
