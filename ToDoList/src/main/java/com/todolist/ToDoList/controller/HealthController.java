package com.todolist.ToDoList.controller;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;

@RestController
@RequestMapping("/health")
@Tag(name = "Health Controller", description = "API for health monitoring of the application")
public class HealthController {

    private final Logger logger = LoggerFactory.getLogger(HealthController.class);

    // Print health on constructor build
    public HealthController() {
        System.out.println("HealthController working");
    }

    /**
     * Endpoint to check the health status of the application.
     * 
     * @return A simple "OK" response indicating the application is healthy.
     */
    @GetMapping
    @Operation(summary = "Health Check", description = "Checks if the application is running and healthy.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Application is healthy."),
        @ApiResponse(responseCode = "500", description = "Application is not healthy.")
    })
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check request received");
        return ResponseEntity.ok("OK");
    }
}
