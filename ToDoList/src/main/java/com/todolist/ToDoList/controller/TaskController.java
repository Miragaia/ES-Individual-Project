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
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser();
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

    // Edit Task - requires authentication and ownership
    @PutMapping("/{id}")
    public ResponseEntity<Task> editTask(@PathVariable UUID id, @RequestBody Task task) {

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser();

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
    public ResponseEntity<Task> getTaskById(@RequestParam UUID id) {

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser();

        // Get the task
        Task task = taskService.getTaskById(id);

        // Check if the task belongs to the authenticated user
        if (user == null || !task.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);  // Forbidden if not the owner
        }

        return ResponseEntity.ok(task);
    }

    // Get All Tasks for Authenticated User
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasksForAuthenticatedUser() {
        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser();
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Get tasks for the authenticated user
        List<Task> tasks = taskService.getTasksByUserId(user.getId());
        return ResponseEntity.ok(tasks);
    }

    // Get Tasks by User and Category - Requires authentication and ownership
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Task>> getTasksByCategory(@RequestParam UUID categoryId) {
        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser();
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Get tasks by category for the authenticated user
        List<Task> tasks = taskService.getTasksByUserAndCategory(user.getId(), categoryId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}/category/{categoryId}")
    public ResponseEntity<List<Task>> getTasksByUserAndCategory(@RequestParam UUID userId, @RequestParam UUID categoryId) {
        List<Task> tasks = taskService.getTasksByUserAndCategory(userId, categoryId);
        return ResponseEntity.ok(tasks);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@RequestParam UUID id) {

        Task task = taskService.getTaskById(id);

        User user = authHandler.getAuthenticatedUser();

        if (user == null || !task.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
