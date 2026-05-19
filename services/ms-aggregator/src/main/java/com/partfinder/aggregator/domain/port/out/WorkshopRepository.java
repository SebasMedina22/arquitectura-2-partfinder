package com.partfinder.aggregator.domain.port.out;

import com.partfinder.aggregator.domain.model.Workshop;
import com.partfinder.aggregator.domain.model.WorkshopId;

import java.util.Optional;

public interface WorkshopRepository {
    Optional<Workshop> findById(WorkshopId id);
    void save(Workshop workshop);
}
