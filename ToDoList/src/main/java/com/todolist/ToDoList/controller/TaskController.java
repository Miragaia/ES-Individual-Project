package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.Status;
import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.AuthHandler;
import com.todolist.ToDoList.service.TaskService;
import com.todolist.ToDoList.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.security.access.prepost.PreAuthorize;
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

    // Create Task - requires authentication    - done
    @PostMapping
    public ResponseEntity<Task> createTask(@AuthenticationPrincipal Jwt jwt, @RequestBody Task task) {

        System.out.println("Inside createTask");

        String userSub = jwt.getClaim("sub");
        System.out.println("User Sub: " + userSub);

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);
        System.out.println("User: " + user);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        // Set the user who created the task
        task.setUser(user);
        task.setStatus(Status.TO_DO);
        Task createdTask = taskService.createTask(task);
        return ResponseEntity.ok(createdTask);
    }

    // Edit Task - requires authentication and ownership    - done
    @PutMapping("/{id}")
    public ResponseEntity<Task> editTask(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id, @RequestBody Task task) {

        String userSub = jwt.getClaim("sub");
        System.out.println("User Sub: " + userSub);

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        // Check if the task belongs to the authenticated user
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  // Forbidden if user is not authenticated
        }

        // Get the task and check if it belongs to the user
        Task existingTask = taskService.getTaskById(id);
        if (!existingTask.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  // Forbidden if not the owner
        }

        Task updatedTask = taskService.updateTask(existingTask, task);
        return ResponseEntity.ok(updatedTask);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID id) {

        String userSub = jwt.getClaim("sub");
        System.out.println("User Sub: " + userSub);

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        // Get the task
        Task task = taskService.getTaskById(id);

        // Check if the task belongs to the authenticated user
        if (user == null || !task.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  // Forbidden if not the owner
        }

        return ResponseEntity.ok(task);
    }

    // Get All Tasks for Authenticated User - done
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasksForAuthenticatedUser(@AuthenticationPrincipal Jwt jwt) {
        System.out.println("Inside getAllTasksForAuthenticatedUser");

        //get all jwt claims
        String jwtClaims = jwt.getClaims().toString();
        System.out.println("JWT Claims: " + jwtClaims);

        System.out.println("JWT: " + jwt);
        String userSub = jwt.getClaim("sub");
        System.out.println("User Sub: " + userSub);

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);
        System.out.println("UserTaskController: " + user);
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
        System.out.println("User Sub: " + userSub);

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
        System.out.println("User Sub: " + userSub);

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        // Get the task
        Task task = taskService.getTaskById(id);

        // Check if the task belongs to the authenticated user
        if (user == null || !task.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  // Forbidden if not the owner
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
