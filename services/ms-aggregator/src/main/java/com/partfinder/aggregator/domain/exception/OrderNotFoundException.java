package com.partfinder.aggregator.domain.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) { super(message); }
}
