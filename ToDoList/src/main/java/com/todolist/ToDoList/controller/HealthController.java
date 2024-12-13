package com.todolist.ToDoList.controller;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

@RestController
@RequestMapping("/health")
public class HealthController {

    private final Logger logger = LoggerFactory.getLogger(HealthController.class);

    //print health on constructur build
    public HealthController() {
        System.out.println("HealthController working");
    } 

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check request received");
        return ResponseEntity.ok("OK");
    }
}
