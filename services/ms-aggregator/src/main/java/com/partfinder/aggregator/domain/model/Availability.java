package com.partfinder.aggregator.domain.model;

/**
 * Estado de disponibilidad de un repuesto frente a una busqueda.
 *
 * AVAILABLE   — InventoryDirect respondio en menos de 800ms y hay stock > 0 en al menos un proveedor.
 * UNCERTAIN   — InventoryDirect tardo mas de 800ms o fallo: R1 del caso ("Disponibilidad Incierta").
 *               No bloqueamos al usuario, le devolvemos el resultado con esta marca.
 * NOT_FOUND   — La parte no aparece en el catalogo Y no hay stock; dispara R3 (notificacion async a TrendCollector).
 */
public enum Availability {
    AVAILABLE, UNCERTAIN, NOT_FOUND
}
