package com.partfinder.trends.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "failed_searches")
public class FailedSearchJpaEntity {

    @Id
    @Column(name = "event_id", length = 64, nullable = false, columnDefinition = "VARCHAR(64)")
    private String eventId;

    @Column(name = "part_query", length = 200, nullable = false, columnDefinition = "VARCHAR(200)")
    private String partQuery;

    @Column(name = "workshop_id", length = 40, nullable = false, columnDefinition = "VARCHAR(40)")
    private String workshopId;

    @Column(name = "searched_at", nullable = false)
    private Instant searchedAt;

    protected FailedSearchJpaEntity() {}

    public FailedSearchJpaEntity(String eventId, String partQuery, String workshopId, Instant searchedAt) {
        this.eventId = eventId;
        this.partQuery = partQuery;
        this.workshopId = workshopId;
        this.searchedAt = searchedAt;
    }

    public String getEventId() { return eventId; }
    public String getPartQuery() { return partQuery; }
    public String getWorkshopId() { return workshopId; }
    public Instant getSearchedAt() { return searchedAt; }
}
