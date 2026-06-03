package com.partfinder.aggregator.infrastructure.persistence.repository;

import com.partfinder.aggregator.infrastructure.persistence.jpa.OrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, String> {
    List<OrderJpaEntity> findByWorkshopId(String workshopId);
    List<OrderJpaEntity> findBySupplierId(String supplierId);
}
