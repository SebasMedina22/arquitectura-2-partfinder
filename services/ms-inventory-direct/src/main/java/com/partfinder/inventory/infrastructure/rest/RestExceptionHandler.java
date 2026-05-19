package com.partfinder.inventory.infrastructure.rest;

import com.partfinder.inventory.infrastructure.rest.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadArg(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse("INVALID_INPUT", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().stream()
                .findFirst().map(e -> e.getDefaultMessage()).orElse("Validation failed");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("VALIDATION_ERROR", msg));
    }
}
