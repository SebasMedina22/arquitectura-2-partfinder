package com.partfinder.trends.infrastructure.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Registra el endpoint WebSocket de tendencias en vivo en /ws/trends.
 * El Nginx Gateway lo expone como /api/trends/ws/trends (con cabeceras Upgrade).
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TrendWebSocketHandler handler;

    public WebSocketConfig(TrendWebSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/trends").setAllowedOriginPatterns("*");
    }
}
