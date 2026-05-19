package com.partfinder.inventory.infrastructure.seed;

import com.partfinder.inventory.application.usecase.UpsertInventoryUseCase;
import com.partfinder.inventory.domain.model.PartId;
import com.partfinder.inventory.domain.model.StockQuantity;
import com.partfinder.inventory.domain.model.SupplierId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Carga un catalogo de demo. Mezcla:
 *  - Partes con varios proveedores (caso normal de busqueda)
 *  - Una parte con stock 0 en todos (NOT_FOUND -> dispara R3)
 *  - Una parte con un solo proveedor (poca disponibilidad)
 */
@Component
@ConditionalOnProperty(name = "inventory.seed.enabled", havingValue = "true", matchIfMissing = true)
public class SeedDataRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataRunner.class);

    private final UpsertInventoryUseCase upsert;

    public SeedDataRunner(UpsertInventoryUseCase upsert) {
        this.upsert = upsert;
    }

    @Override
    public void run(String... args) {
        // Filtro de aceite — varias bodegas, stock variado
        seed("PRT-FO-001", "SUP-LIMA",   25);
        seed("PRT-FO-001", "SUP-MEDLN",  12);
        seed("PRT-FO-001", "SUP-CALI",    3);

        // Bujias — disponibilidad amplia
        seed("PRT-BG-002", "SUP-LIMA",   50);
        seed("PRT-BG-002", "SUP-MEDLN",  30);
        seed("PRT-BG-002", "SUP-CALI",   18);
        seed("PRT-BG-002", "SUP-BOGOTA", 40);

        // Pastillas freno — solo un proveedor
        seed("PRT-PF-003", "SUP-BOGOTA",  8);

        // Embrague — sin stock en nadie (forzar NOT_FOUND -> R3 publica a TrendCollector)
        seed("PRT-EM-099", "SUP-LIMA",    0);
        seed("PRT-EM-099", "SUP-MEDLN",   0);

        log.info("Seed: catalogo de demo cargado (5 partes, 4 proveedores).");
    }

    private void seed(String partId, String supplierId, int stock) {
        upsert.execute(new PartId(partId), new SupplierId(supplierId), StockQuantity.of(stock));
    }
}
