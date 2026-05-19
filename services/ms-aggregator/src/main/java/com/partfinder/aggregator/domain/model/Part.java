package com.partfinder.aggregator.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio: una parte del catalogo. Vive en Elasticsearch para
 * busqueda full-text. El dominio NO conoce ES — eso es solo el adaptador.
 */
public class Part {

    private final PartId id;
    private final String name;
    private final String description;
    private final String category;
    private final Money referencePrice;

    public Part(PartId id, String name, String description, String category, Money referencePrice) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.description = description == null ? "" : description;
        this.category = category == null ? "" : category;
        this.referencePrice = Objects.requireNonNull(referencePrice);
        if (name.isBlank()) throw new IllegalArgumentException("name no puede ser vacio");
    }

    public PartId id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public String category() { return category; }
    public Money referencePrice() { return referencePrice; }
}
