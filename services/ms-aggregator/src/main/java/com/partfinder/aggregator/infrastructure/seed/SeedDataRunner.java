package com.partfinder.aggregator.infrastructure.seed;

import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Part;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.model.Workshop;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.port.out.PartCatalogRepository;
import com.partfinder.aggregator.domain.port.out.WorkshopRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Carga datos de demo:
 *  - 3 workshops con distintos niveles de credito (WS-001 OK, WS-002 al 80%, WS-003 excedido para R2)
 *  - 5 partes en el catalogo de Elasticsearch
 */
@Component
@ConditionalOnProperty(name = "aggregator.seed.enabled", havingValue = "true", matchIfMissing = true)
public class SeedDataRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedDataRunner.class);

    private final WorkshopRepository workshops;
    private final PartCatalogRepository catalog;

    public SeedDataRunner(WorkshopRepository workshops, PartCatalogRepository catalog) {
        this.workshops = workshops;
        this.catalog = catalog;
    }

    @Override
    public void run(String... args) {
        seedWorkshops();
        seedCatalog();
    }

    private void seedWorkshops() {
        // WS-001: solvente
        upsert(new Workshop(new WorkshopId("WS-001"), "Taller La Esquina",
                Money.cop(new BigDecimal("5000000")), Money.cop(new BigDecimal("500000"))));

        // WS-002: cerca del limite (80% usado), aun no bloquea R2
        upsert(new Workshop(new WorkshopId("WS-002"), "Mecanica San Jose",
                Money.cop(new BigDecimal("3000000")), Money.cop(new BigDecimal("2400000"))));

        // WS-003: cupo excedido — R2 bloquea cualquier pedido nuevo
        upsert(new Workshop(new WorkshopId("WS-003"), "Auto Servicios Bolivar",
                Money.cop(new BigDecimal("2000000")), Money.cop(new BigDecimal("2200000"))));

        log.info("Seed: 3 workshops cargados (WS-001 OK, WS-002 al 80%, WS-003 excedido).");
    }

    private void upsert(Workshop w) {
        if (workshops.findById(w.id()).isEmpty()) {
            workshops.save(w);
        }
    }

    private void seedCatalog() {
        save("PRT-FO-001", "Filtro de aceite Toyota Corolla 2018",
                "Filtro de aceite compatible con motores 1.8 de la linea Toyota Corolla.", "filtros",
                new BigDecimal("35000"));
        save("PRT-BG-002", "Bujias NGK Iridium",
                "Set de 4 bujias de iridio para motores a gasolina, larga duracion.", "bujias",
                new BigDecimal("180000"));
        save("PRT-PF-003", "Pastillas de freno delanteras Honda Civic",
                "Pastillas ceramicas para Civic 2016-2020, par delantero.", "frenos",
                new BigDecimal("220000"));
        save("PRT-EM-099", "Embrague Mazda CX-5",
                "Kit de embrague completo para Mazda CX-5 2.0L. Inventario tipicamente bajo.", "transmision",
                new BigDecimal("1200000"));
        save("PRT-AC-010", "Aceite motor sintetico 5W30 4L",
                "Aceite sintetico premium 5W30 presentacion 4 litros.", "lubricantes",
                new BigDecimal("110000"));
        log.info("Seed: catalogo de partes cargado en Elasticsearch (5 partes).");
    }

    private void save(String id, String name, String desc, String category, BigDecimal price) {
        catalog.save(new Part(new PartId(id), name, desc, category, Money.cop(price)));
    }
}
