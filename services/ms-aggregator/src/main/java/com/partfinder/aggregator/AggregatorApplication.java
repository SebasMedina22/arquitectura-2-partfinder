package com.partfinder.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.partfinder.aggregator.infrastructure.client")
@EnableScheduling
public class AggregatorApplication {
    public static void main(String[] args) {
        SpringApplication.run(AggregatorApplication.class, args);
    }
}
