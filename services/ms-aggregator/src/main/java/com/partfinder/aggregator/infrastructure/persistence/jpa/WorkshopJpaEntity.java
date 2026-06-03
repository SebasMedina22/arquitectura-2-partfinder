package com.partfinder.aggregator.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "workshops")
public class WorkshopJpaEntity {

    @Id
    @Column(name = "id", length = 40, columnDefinition = "VARCHAR(40)")
    private String id;

    @Column(name = "name", length = 120, nullable = false, columnDefinition = "VARCHAR(120)")
    private String name;

    @Column(name = "credit_limit_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal creditLimitAmount;

    @Column(name = "credit_limit_currency", length = 3, nullable = false, columnDefinition = "VARCHAR(3)")
    private String creditLimitCurrency;

    @Column(name = "credit_used_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal creditUsedAmount;

    @Column(name = "credit_used_currency", length = 3, nullable = false, columnDefinition = "VARCHAR(3)")
    private String creditUsedCurrency;

    protected WorkshopJpaEntity() {}

    public WorkshopJpaEntity(String id, String name,
                             BigDecimal creditLimitAmount, String creditLimitCurrency,
                             BigDecimal creditUsedAmount, String creditUsedCurrency) {
        this.id = id; this.name = name;
        this.creditLimitAmount = creditLimitAmount; this.creditLimitCurrency = creditLimitCurrency;
        this.creditUsedAmount = creditUsedAmount; this.creditUsedCurrency = creditUsedCurrency;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public BigDecimal getCreditLimitAmount() { return creditLimitAmount; }
    public String getCreditLimitCurrency() { return creditLimitCurrency; }
    public BigDecimal getCreditUsedAmount() { return creditUsedAmount; }
    public String getCreditUsedCurrency() { return creditUsedCurrency; }

    public void setCreditUsedAmount(BigDecimal v) { this.creditUsedAmount = v; }
    public void setCreditLimitAmount(BigDecimal v) { this.creditLimitAmount = v; }
}
