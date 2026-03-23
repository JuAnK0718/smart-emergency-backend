package com.sigeu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global error handler.
 * Returns clean JSON error responses instead of raw Spring stack traces.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles @Valid validation failures (missing required fields, etc.)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("error",   "Datos inválidos");
        body.put("details", fieldErrors);
        body.put("status",  400);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handles unexpected runtime errors.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericError(Exception ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error",   "Error interno del servidor");
        body.put("message", ex.getMessage());
        body.put("status",  500);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
