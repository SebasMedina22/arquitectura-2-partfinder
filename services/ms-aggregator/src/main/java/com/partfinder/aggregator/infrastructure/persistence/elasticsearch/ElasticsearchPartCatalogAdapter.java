package com.partfinder.aggregator.infrastructure.persistence.elasticsearch;

import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Part;
import com.partfinder.aggregator.domain.model.PartId;
import com.partfinder.aggregator.domain.port.out.PartCatalogRepository;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Patron Adapter (GoF) lado salida: implementa el puerto PartCatalogRepository
 * con Spring Data Elasticsearch. El dominio no conoce ES.
 */
@Component
public class ElasticsearchPartCatalogAdapter implements PartCatalogRepository {

    private final PartElasticsearchRepository repo;
    private final ElasticsearchOperations operations;

    public ElasticsearchPartCatalogAdapter(PartElasticsearchRepository repo,
                                           ElasticsearchOperations operations) {
        this.repo = repo;
        this.operations = operations;
    }

    @Override
    public Optional<Part> findById(PartId id) {
        return repo.findById(id.value()).map(this::toDomain);
    }

    @Override
    public List<Part> search(String query, int limit) {
        if (query == null || query.isBlank()) return List.of();

        // multi_match sobre name + description con analyzer spanish (lo declara el @Field)
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(MultiMatchQuery.of(m -> m
                        .query(query)
                        .fields("name^3", "description"))))
                .withPageable(PageRequest.of(0, limit))
                .build();

        SearchHits<PartDocument> hits = operations.search(nativeQuery, PartDocument.class);
        return hits.stream().map(h -> toDomain(h.getContent())).toList();
    }

    @Override
    public void save(Part part) {
        PartDocument doc = new PartDocument(
                part.id().value(),
                part.name(),
                part.description(),
                part.category(),
                part.referencePrice().amount(),
                part.referencePrice().currency()
        );
        repo.save(doc);
    }

    private Part toDomain(PartDocument d) {
        return new Part(
                new PartId(d.getId()),
                d.getName(),
                d.getDescription(),
                d.getCategory(),
                new Money(d.getPriceAmount(), d.getPriceCurrency())
        );
    }
}
