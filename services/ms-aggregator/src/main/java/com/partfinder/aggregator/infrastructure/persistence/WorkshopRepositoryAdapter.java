package com.partfinder.aggregator.infrastructure.persistence;

import com.partfinder.aggregator.domain.model.Money;
import com.partfinder.aggregator.domain.model.Workshop;
import com.partfinder.aggregator.domain.model.WorkshopId;
import com.partfinder.aggregator.domain.port.out.WorkshopRepository;
import com.partfinder.aggregator.infrastructure.persistence.jpa.WorkshopJpaEntity;
import com.partfinder.aggregator.infrastructure.persistence.repository.WorkshopJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WorkshopRepositoryAdapter implements WorkshopRepository {

    private final WorkshopJpaRepository jpa;

    public WorkshopRepositoryAdapter(WorkshopJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Workshop> findById(WorkshopId id) {
        return jpa.findById(id.value()).map(this::toDomain);
    }

    @Override
    public void save(Workshop workshop) {
        WorkshopJpaEntity entity = jpa.findById(workshop.id().value())
                .orElseGet(() -> new WorkshopJpaEntity(
                        workshop.id().value(), workshop.name(),
                        workshop.creditLimit().amount(), workshop.creditLimit().currency(),
                        workshop.creditUsed().amount(), workshop.creditUsed().currency()
                ));
        entity.setCreditUsedAmount(workshop.creditUsed().amount());
        entity.setCreditLimitAmount(workshop.creditLimit().amount());
        jpa.save(entity);
    }

    private Workshop toDomain(WorkshopJpaEntity e) {
        return new Workshop(
                new WorkshopId(e.getId()),
                e.getName(),
                new Money(e.getCreditLimitAmount(), e.getCreditLimitCurrency()),
                new Money(e.getCreditUsedAmount(), e.getCreditUsedCurrency())
        );
    }
}
