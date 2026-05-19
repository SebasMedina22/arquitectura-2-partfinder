package com.partfinder.aggregator.infrastructure.rest;

import com.partfinder.aggregator.domain.exception.OrderNotFoundException;
import com.partfinder.aggregator.domain.exception.OrderRuleViolationException;
import com.partfinder.aggregator.domain.exception.WorkshopNotFoundException;
import com.partfinder.aggregator.infrastructure.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(OrderRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleRuleViolation(OrderRuleViolationException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ErrorResponse(ex.ruleCode(), ex.getMessage()));
    }

    @ExceptionHandler(WorkshopNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWorkshopNotFound(WorkshopNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("WORKSHOP_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ORDER_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_INPUT", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().stream()
                .findFirst().map(e -> e.getDefaultMessage()).orElse("Validation failed");
        return ResponseEntity.badRequest().body(new ErrorResponse("VALIDATION_ERROR", msg));
    }
}
