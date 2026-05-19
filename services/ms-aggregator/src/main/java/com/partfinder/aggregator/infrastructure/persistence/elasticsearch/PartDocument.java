package com.partfinder.aggregator.infrastructure.persistence.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

/**
 * Documento de Elasticsearch para el catalogo de partes. Vive en la capa
 * infrastructure (NO en domain), porque tiene anotaciones especificas de ES.
 *
 * El analyzer "spanish" provee stemming y stopwords para que "filtro de aceite"
 * matchee variantes como "filtros aceites".
 */
@Document(indexName = "parts")
public class PartDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "spanish")
    private String name;

    @Field(type = FieldType.Text, analyzer = "spanish")
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal priceAmount;

    @Field(type = FieldType.Keyword)
    private String priceCurrency;

    public PartDocument() {}

    public PartDocument(String id, String name, String description, String category,
                        BigDecimal priceAmount, String priceCurrency) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.priceAmount = priceAmount;
        this.priceCurrency = priceCurrency;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public BigDecimal getPriceAmount() { return priceAmount; }
    public String getPriceCurrency() { return priceCurrency; }
}
