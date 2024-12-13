package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.Status;
import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.AuthHandler;
import com.todolist.ToDoList.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task Controller", description = "API for managing tasks")
public class TaskController {

    private final TaskService taskService;
    private final AuthHandler authHandler;

    public TaskController(TaskService taskService, AuthHandler authHandler) {
        this.taskService = taskService;
        this.authHandler = authHandler;
    }

    @PostMapping
    @Operation(summary = "Create a task", description = "Creates a new task for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task created successfully",
                     content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<Task> createTask(@AuthenticationPrincipal Jwt jwt, @RequestBody Task task) {

        String userSub = jwt.getClaim("sub");

        User user = authHandler.getAuthenticatedUser(userSub);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        task.setUser(user);
        task.setStatus(Status.TO_DO);
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit a task", description = "Updates an existing task for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task updated successfully",
                     content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - Task does not belong to user"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Task> editTask(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody Task task) {

        String userSub = jwt.getClaim("sub");

        User user = authHandler.getAuthenticatedUser(userSub);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Task existingTask = taskService.getTaskById(id);
        if (!existingTask.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Task updatedTask = taskService.updateTask(existingTask, task);
        return ResponseEntity.ok(updatedTask);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a task by ID", description = "Retrieves a task by its ID for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Task retrieved successfully",
                     content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - Task does not belong to user"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Task> getTaskById(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID id) {

        String userSub = jwt.getClaim("sub");

        User user = authHandler.getAuthenticatedUser(userSub);

        Task task = taskService.getTaskById(id);

        if (user == null || !task.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(task);
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully",
                     content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<List<Task>> getAllTasksForAuthenticatedUser(@AuthenticationPrincipal Jwt jwt) {

        String userSub = jwt.getClaim("sub");

        User user = authHandler.getAuthenticatedUser(userSub);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Task> tasks = taskService.getTasksByUserId(user.getId());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter tasks", description = "Filters tasks based on status, category, or sort order.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tasks filtered successfully",
                     content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameter"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<List<Task>> getTasksByFilters(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy) {

        String userSub = jwt.getClaim("sub");

        User user = authHandler.getAuthenticatedUser(userSub);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Task> tasks;
        Status statusEnum = null;

        if (status != null) {
            try {
                statusEnum = Status.valueOf(status);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Task does not belong to user"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> deleteTask(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {

        String userSub = jwt.getClaim("sub");

        User user = authHandler.getAuthenticatedUser(userSub);

        Task task = taskService.getTaskById(id);

        if (user == null || !task.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
