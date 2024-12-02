package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.Status;
import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.AuthHandler;
import com.todolist.ToDoList.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final AuthHandler authHandler;

    public TaskController(TaskService taskService, AuthHandler authHandler) {
        this.taskService = taskService;
        this.authHandler = authHandler;
    }

    // Create Task - requires authentication
    @PostMapping
    public ResponseEntity<Task> createTask(@AuthenticationPrincipal Jwt jwt, @RequestBody Task task) {

        String userSub = jwt.getClaim("sub");

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        // Set the user who created the task
        task.setUser(user);
        task.setStatus(Status.TO_DO);
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    // Edit Task - requires authentication and ownership
    @PutMapping("/{id}")
    public ResponseEntity<Task> editTask(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody Task task) {

        String userSub = jwt.getClaim("sub");

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        // Check if the task belongs to the authenticated user
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Get the task and check if it belongs to the user
        Task existingTask = taskService.getTaskById(id);
        if (!existingTask.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); 
        }

        Task updatedTask = taskService.updateTask(existingTask, task);
        return ResponseEntity.ok(updatedTask);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID id) {

        String userSub = jwt.getClaim("sub");

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        // Get the task
        Task task = taskService.getTaskById(id);

        // Check if the task belongs to the authenticated user
        if (user == null || !task.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); 
        }

        return ResponseEntity.ok(task);
    }

    // Get All Tasks for Authenticated User - done
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasksForAuthenticatedUser(@AuthenticationPrincipal Jwt jwt) {

        String userSub = jwt.getClaim("sub");

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Get tasks for the authenticated user
        List<Task> tasks = taskService.getTasksByUserId(user.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Task>> getTasksByFilters(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy) {

        String userSub = jwt.getClaim("sub");

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Task> tasks;
        Status statusEnum = null;

        // Validate status if present
        if (status != null) {
            try {
                statusEnum = Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        // Fetch tasks based on filters
        if (statusEnum != null && categoryId != null) {
            tasks = taskService.getTasksByStatusCategoryAndUser(statusEnum, categoryId, user, sortBy);
        } else if (statusEnum != null) {
            tasks = taskService.getTasksByStatusAndUser(statusEnum, user, sortBy);
        } else if (categoryId != null) {
            tasks = taskService.getTasksByCategoryAndUser(categoryId, user, sortBy);
        } else {
            tasks = taskService.getTasksByUser(user, sortBy);
        }

        return ResponseEntity.ok(tasks);
    }



    // Delete Task - done
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

        String userSub = jwt.getClaim("sub");
        
        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        // Get the task
        Task task = taskService.getTaskById(id);

        // Check if the task belongs to the authenticated user
        if (user == null || !task.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
