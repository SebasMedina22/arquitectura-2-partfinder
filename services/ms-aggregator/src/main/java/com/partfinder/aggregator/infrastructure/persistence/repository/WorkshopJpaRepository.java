package com.partfinder.aggregator.infrastructure.persistence.repository;

import com.partfinder.aggregator.infrastructure.persistence.jpa.WorkshopJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopJpaRepository extends JpaRepository<WorkshopJpaEntity, String> {
}
