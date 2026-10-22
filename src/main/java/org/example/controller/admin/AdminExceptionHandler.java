package org.example.controller.admin;

import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice(basePackages = "org.example.controller.admin")
public class AdminExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> conflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("status", 409,
                "message", "The data already exists or is referenced by another record. Refresh and try again."));
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> invalid(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("status", exception.getStatusCode().value(),
                "message", exception.getReason() == null ? "Request failed" : exception.getReason()));
    }
}
