package com.placement.placementmanagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ApiError> build(
            HttpStatusCode status,
            String message,
            Map<String, String> errors) {

        return ResponseEntity
                .status(status)
                .body(new ApiError(status.value(), message, errors));
    }

    // @Valid failures on request bodies
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return build(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    // Errors thrown deliberately in services (404, 409, 403, 400 ...)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(
            ResponseStatusException ex) {

        String message = ex.getReason() != null
                ? ex.getReason()
                : "Request failed";

        return build(ex.getStatusCode(), message, new HashMap<>());
    }

    // Database constraint problems (duplicate, referenced record ...)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());

        return build(
                HttpStatus.CONFLICT,
                "Operation conflicts with existing data "
                        + "(duplicate or referenced record)",
                new HashMap<>());
    }

    // Broken or missing JSON body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableBody(
            HttpMessageNotReadableException ex) {

        return build(
                HttpStatus.BAD_REQUEST,
                "Malformed or unreadable request body",
                new HashMap<>());
    }

    // e.g. /api/students/abc where a number is expected
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        return build(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter '" + ex.getName() + "'",
                new HashMap<>());
    }

    // Safety net: anything not handled above
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAnyOtherException(Exception ex) {

        // Spring's own web errors (404 unknown URL, 405 wrong method,
        // missing parameter ...) already carry the right status code.
        if (ex instanceof org.springframework.web.ErrorResponse springError) {
            String detail = springError.getBody().getDetail();
            return build(
                    springError.getStatusCode(),
                    detail != null ? detail : "Request failed",
                    new HashMap<>());
        }

        log.error("Unexpected error", ex);

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong on the server",
                new HashMap<>());
    }
}