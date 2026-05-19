package com.partfinder.aggregator.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topologia AMQP — lado publicador del Aggregator:
 *  - Exchange `partfinder.search.events` (topic): publica SearchFailedEvent
 *    con routing key `search.failed` para que MS-TrendCollector lo consuma.
 */
@Configuration
public class RabbitMqConfig {

    public static final String SEARCH_EXCHANGE = "partfinder.search.events";
    public static final String SEARCH_FAILED_ROUTING_KEY = "search.failed";

    @Bean
    public TopicExchange searchExchange() {
        return new TopicExchange(SEARCH_EXCHANGE, true, false);
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
        template.setMandatory(true);
        return template;
    }
}
