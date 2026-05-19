package com.partfinder.trends.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia AMQP — lado consumidor de TrendCollector:
 *  - Exchange `partfinder.search.events` (topic) publica eventos del Aggregator.
 *  - Cola `trends.search-failed.q` (con DLQ) escucha la routing key `search.failed`.
 *
 * Los nombres deben coincidir con los que declara Aggregator en su RabbitMqConfig.
 */
@Configuration
public class RabbitMqConfig {

    public static final String SEARCH_EXCHANGE = "partfinder.search.events";
    public static final String SEARCH_FAILED_ROUTING_KEY = "search.failed";

    public static final String TRENDS_FAILED_QUEUE = "trends.search-failed.q";
    public static final String TRENDS_FAILED_DLQ   = "trends.search-failed.dlq";
    public static final String DLX_EXCHANGE        = "partfinder.trends.dlx";

    @Bean
    public TopicExchange searchExchange() {
        return new TopicExchange(SEARCH_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange trendsDlx() {
        return new TopicExchange(DLX_EXCHANGE, true, false);
    }

    @Bean
    public Queue trendsFailedQueue() {
        return QueueBuilder.durable(TRENDS_FAILED_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TRENDS_FAILED_DLQ)
                .build();
    }

    @Bean
    public Queue trendsFailedDlq() {
        return QueueBuilder.durable(TRENDS_FAILED_DLQ).build();
    }

    @Bean
    public Binding trendsFailedBinding(Queue trendsFailedQueue, TopicExchange searchExchange) {
        return BindingBuilder.bind(trendsFailedQueue).to(searchExchange).with(SEARCH_FAILED_ROUTING_KEY);
    }

    @Bean
    public Binding trendsFailedDlqBinding(Queue trendsFailedDlq, TopicExchange trendsDlx) {
        return BindingBuilder.bind(trendsFailedDlq).to(trendsDlx).with(TRENDS_FAILED_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.findAndRegisterModules();
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}
