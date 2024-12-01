package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.Category;
import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.CategoryService;
import com.todolist.ToDoList.service.TaskService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.todolist.ToDoList.service.AuthHandler;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final TaskService taskService;
    private final AuthHandler authHandler;

    public CategoryController(CategoryService categoryService, AuthHandler authHandler, TaskService taskService) {
        this.categoryService = categoryService;
        this.authHandler = authHandler;
        this.taskService = taskService;
    }

    @PostMapping  
    public ResponseEntity<Category> createCategory(@AuthenticationPrincipal Jwt jwt, @RequestBody Category category) {

        String userSub = jwt.getClaim("sub");

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        category.setUser(user);

        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Category>> getUserCategories(@AuthenticationPrincipal Jwt jwt) {

        String userSub = jwt.getClaim("sub");

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Category> categories = categoryService.getCategoriesByUser(user);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{taskId}/category/{categoryId}")
    public ResponseEntity<Task> assignCategoryToTask(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID taskId,
            @PathVariable UUID categoryId) {

        String userSub = jwt.getClaim("sub");

        // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser(userSub);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Retrieve the task and category
        Task existingTask = taskService.getTaskById(taskId);
        Category category = categoryService.getCategoryById(categoryId);

        // Check if the task and category exist and belong to the user
        if (existingTask == null || category == null 
            || !existingTask.getUser().getId().equals(user.getId()) 
            || !category.getUser().getId().equals(user.getId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Update the task's category
        existingTask.setCategory(category);

        // Save the updated task with the new category
        Task updatedTask = taskService.updateTask(existingTask, existingTask);

        return ResponseEntity.ok(updatedTask);
    }


    @GetMapping("/{id}")    
    public ResponseEntity<Category> getCategoryById(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
