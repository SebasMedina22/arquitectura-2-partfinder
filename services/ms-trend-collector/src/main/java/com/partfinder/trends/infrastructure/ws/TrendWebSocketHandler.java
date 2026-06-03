package com.partfinder.trends.infrastructure.ws;

import com.partfinder.trends.domain.event.SearchFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Empuja en vivo (WebSocket) cada nueva busqueda fallida que el consumer registra.
 * El navegador se conecta y refresca las tendencias sin necesidad de "Refrescar".
 * Hace visible el flujo asincrono R3 de punta a punta en tiempo real.
 */
@Component
public class TrendWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(TrendWebSocketHandler.class);

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.debug("WS conectado: {} ({} sesiones)", session.getId(), sessions.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    /** Difunde el evento a todos los clientes conectados. Best-effort: nunca propaga error. */
    public void broadcast(SearchFailedEvent event) {
        if (sessions.isEmpty()) return;
        String json = "{\"type\":\"trend\",\"partQuery\":\"" + escape(event.partQuery())
                + "\",\"workshopId\":\"" + escape(event.workshopId()) + "\"}";
        TextMessage msg = new TextMessage(json);
        for (WebSocketSession s : sessions) {
            try {
                if (s.isOpen()) s.sendMessage(msg);
            } catch (IOException e) {
                log.debug("WS envio fallo a {}: {}", s.getId(), e.getMessage());
            }
        }
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
