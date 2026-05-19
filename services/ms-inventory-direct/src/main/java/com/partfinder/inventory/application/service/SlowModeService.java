package com.partfinder.inventory.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Mantiene un "modo lento" configurable en runtime. Cuando esta activo, cada
 * GET /inventory hace un Thread.sleep(delayMs) ANTES de responder.
 *
 * Para que sirve: forzar el escenario R1 desde el lado de InventoryDirect sin
 * tocar el codigo del Aggregator. El profesor puede ver en vivo como Aggregator
 * cambia el availability a UNCERTAIN cuando subimos el delay por encima de 800ms.
 */
@Component
public class SlowModeService {

    private final AtomicLong delayMs;

    public SlowModeService(@Value("${inventory.slow-mode.initial-delay-ms:0}") long initialDelayMs) {
        this.delayMs = new AtomicLong(initialDelayMs);
    }

    public long getDelayMs() {
        return delayMs.get();
    }

    public void setDelayMs(long ms) {
        if (ms < 0) throw new IllegalArgumentException("delayMs no puede ser negativo");
        this.delayMs.set(ms);
    }

    /** Bloquea el thread actual el tiempo configurado (si > 0). Manejado en el controller. */
    public void applyDelayIfAny() {
        long ms = delayMs.get();
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
