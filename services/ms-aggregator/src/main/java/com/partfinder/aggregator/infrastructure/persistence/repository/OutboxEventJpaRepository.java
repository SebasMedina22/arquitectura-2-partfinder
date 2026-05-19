package com.partfinder.aggregator.infrastructure.persistence.repository;

import com.partfinder.aggregator.infrastructure.persistence.jpa.OutboxEventJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, Long> {

    @Query("select e from OutboxEventJpaEntity e where e.publishedAt is null order by e.id asc")
    List<OutboxEventJpaEntity> findPending(Pageable pageable);
}
