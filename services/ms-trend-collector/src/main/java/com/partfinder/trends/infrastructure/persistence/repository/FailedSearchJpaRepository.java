package com.partfinder.trends.infrastructure.persistence.repository;

import com.partfinder.trends.infrastructure.persistence.jpa.FailedSearchJpaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface FailedSearchJpaRepository extends JpaRepository<FailedSearchJpaEntity, String> {

    /**
     * Devuelve filas con: partQuery, count, firstSeen, lastSeen — ordenadas desc por count.
     */
    @Query("""
        select e.partQuery as partQuery,
               count(e)    as failCount,
               min(e.searchedAt) as firstSeen,
               max(e.searchedAt) as lastSeen
        from FailedSearchJpaEntity e
        group by e.partQuery
        order by count(e) desc
        """)
    List<TrendRow> aggregateTrends(Pageable pageable);

    interface TrendRow {
        String getPartQuery();
        long getFailCount();
        Instant getFirstSeen();
        Instant getLastSeen();
    }
}
