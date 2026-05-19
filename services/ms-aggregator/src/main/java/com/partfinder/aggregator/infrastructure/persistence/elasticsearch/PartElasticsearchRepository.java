package com.partfinder.aggregator.infrastructure.persistence.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PartElasticsearchRepository extends ElasticsearchRepository<PartDocument, String> {
}
