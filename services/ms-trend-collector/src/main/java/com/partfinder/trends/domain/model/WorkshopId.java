package com.partfinder.trends.domain.model;

import java.util.Objects;
import java.util.regex.Pattern;

public final class WorkshopId {

    private static final Pattern PATTERN = Pattern.compile("^WS-[A-Z0-9-]{1,32}$");
    private final String value;

    public WorkshopId(String value) {
        Objects.requireNonNull(value);
        String upper = value.toUpperCase();
        if (!PATTERN.matcher(upper).matches()) {
            throw new IllegalArgumentException("WorkshopId invalido: " + value + " (esperado WS-XXX)");
        }
        this.value = upper;
    }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkshopId other)) return false;
        return value.equals(other.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
