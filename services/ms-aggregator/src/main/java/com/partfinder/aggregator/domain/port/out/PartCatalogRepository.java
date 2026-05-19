package com.partfinder.aggregator.domain.port.out;

import com.partfinder.aggregator.domain.model.Part;
import com.partfinder.aggregator.domain.model.PartId;

import java.util.List;
import java.util.Optional;

/**
 * Puerto: catalogo de partes (en Elasticsearch). Permite busqueda full-text
 * y consulta directa por id.
 */
public interface PartCatalogRepository {

    Optional<Part> findById(PartId id);

    /** Busqueda full-text por nombre/descripcion/categoria. */
    List<Part> search(String query, int limit);

    void save(Part part);
}
