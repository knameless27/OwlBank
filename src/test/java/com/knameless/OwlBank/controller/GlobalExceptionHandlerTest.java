package com.knameless.OwlBank.controller;

import com.knameless.OwlBank.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleException_ShouldReturnInternalServerError() {
        Exception exception = new Exception("Unexpected error");
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server error: Unexpected error", response.getBody().getMessage());
    }

    @Test
    void handleNotFoundException_ShouldReturnNotFound() {
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found");
        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("404 NOT_FOUND \"Resource not found\"", response.getBody().getMessage());
    }
}