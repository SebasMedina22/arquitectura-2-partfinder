package com.partfinder.aggregator.application.config;

import com.partfinder.aggregator.application.usecase.CreateOrderUseCase;
import com.partfinder.aggregator.application.usecase.GetOrderUseCase;
import com.partfinder.aggregator.application.usecase.ListWorkshopOrdersUseCase;
import com.partfinder.aggregator.application.usecase.SearchPartUseCase;
import com.partfinder.aggregator.domain.factory.SearchResultFactory;
import com.partfinder.aggregator.domain.policy.OrderCreditPolicy;
import com.partfinder.aggregator.domain.policy.OrderRulePolicy;
import com.partfinder.aggregator.domain.port.out.DomainEventPublisher;
import com.partfinder.aggregator.domain.port.out.InventoryDirectPort;
import com.partfinder.aggregator.domain.port.out.OrderRepository;
import com.partfinder.aggregator.domain.port.out.PartCatalogRepository;
import com.partfinder.aggregator.domain.port.out.WorkshopRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;

@Configuration
public class UseCaseConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public SearchResultFactory searchResultFactory() {
        return new SearchResultFactory();
    }

    @Bean
    public OrderCreditPolicy orderCreditPolicy() {
        return new OrderCreditPolicy();
    }

    @Bean
    public SearchPartUseCase searchPartUseCase(PartCatalogRepository catalog,
                                               InventoryDirectPort inventory,
                                               SearchResultFactory factory,
                                               DomainEventPublisher publisher,
                                               Clock clock) {
        return new SearchPartUseCase(catalog, inventory, factory, publisher, clock, 10);
    }

    @Bean
    public CreateOrderUseCase createOrderUseCase(OrderCreditPolicy creditPolicy,
                                                 WorkshopRepository workshops,
                                                 OrderRepository orders,
                                                 Clock clock) {
        List<OrderRulePolicy> policies = List.of(creditPolicy);
        return new CreateOrderUseCase(policies, workshops, orders, clock);
    }

    @Bean
    public GetOrderUseCase getOrderUseCase(OrderRepository orders) {
        return new GetOrderUseCase(orders);
    }

    @Bean
    public ListWorkshopOrdersUseCase listWorkshopOrdersUseCase(OrderRepository orders) {
        return new ListWorkshopOrdersUseCase(orders);
    }
}
