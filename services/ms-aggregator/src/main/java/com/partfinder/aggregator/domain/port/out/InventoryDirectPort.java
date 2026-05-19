package com.partfinder.aggregator.domain.port.out;

import com.partfinder.aggregator.domain.model.InventoryQueryResult;
import com.partfinder.aggregator.domain.model.PartId;

/**
 * Puerto de salida hacia MS-InventoryDirect. El dominio NO conoce Feign ni HTTP.
 * El Adapter materializa R1: si la llamada tarda > 800ms marca el resultado como
 * timedOut=true para que el caso de uso lo traduzca a Availability.UNCERTAIN.
 */
public interface InventoryDirectPort {

    InventoryQueryResult fetchInventoryFor(PartId partId);
}
