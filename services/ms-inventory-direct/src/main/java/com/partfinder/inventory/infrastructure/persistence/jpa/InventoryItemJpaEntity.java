package com.partfinder.inventory.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "inventory_items")
public class InventoryItemJpaEntity {

    @Embeddable
    public static class Id implements Serializable {
        @Column(name = "part_id", length = 40, nullable = false, columnDefinition = "VARCHAR(40)")
        private String partId;
        @Column(name = "supplier_id", length = 40, nullable = false, columnDefinition = "VARCHAR(40)")
        private String supplierId;

        public Id() {}
        public Id(String partId, String supplierId) { this.partId = partId; this.supplierId = supplierId; }
        public String getPartId() { return partId; }
        public String getSupplierId() { return supplierId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id id)) return false;
            return Objects.equals(partId, id.partId) && Objects.equals(supplierId, id.supplierId);
        }
        @Override public int hashCode() { return Objects.hash(partId, supplierId); }
    }

    @EmbeddedId
    private Id id;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    protected InventoryItemJpaEntity() {}

    public InventoryItemJpaEntity(String partId, String supplierId, int stock, Instant lastUpdated) {
        this.id = new Id(partId, supplierId);
        this.stock = stock;
        this.lastUpdated = lastUpdated;
    }

    public Id getId() { return id; }
    public int getStock() { return stock; }
    public Instant getLastUpdated() { return lastUpdated; }

    public void setStock(int stock) { this.stock = stock; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
