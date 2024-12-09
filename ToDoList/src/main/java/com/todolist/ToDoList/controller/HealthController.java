package com.todolist.ToDoList.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/health")
public class HealthController {

    //print health on constructur build
    public HealthController() {
        System.out.println("HealthController working");
    } 

    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }
}
