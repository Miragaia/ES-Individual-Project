package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.Category;
import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.CategoryService;
import com.todolist.ToDoList.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Category Controller", description = "API for managing categories and their assignment to tasks")
public class CategoryController {

    private final CategoryService categoryService;
    private final TaskService taskService;
    private final AuthHandler authHandler;

    public CategoryController(CategoryService categoryService, AuthHandler authHandler, TaskService taskService) {
        this.categoryService = categoryService;
        this.authHandler = authHandler;
        this.taskService = taskService;
    }

    @Operation(summary = "Create a new category", description = "Creates a category for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category created successfully", content = @Content(schema = @Schema(implementation = Category.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
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

    @Operation(summary = "Get user categories", description = "Retrieves all categories for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully", content = @Content(schema = @Schema(implementation = List.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
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

    @Operation(summary = "Assign a category to a task", description = "Associates a category with a specific task for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category assigned to task successfully", content = @Content(schema = @Schema(implementation = Task.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
    })
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

    @Operation(summary = "Get a category by ID", description = "Fetches a specific category by its ID for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category retrieved successfully", content = @Content(schema = @Schema(implementation = Category.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @Operation(summary = "Delete a category", description = "Deletes a category by its ID for the authenticated user.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Category deleted successfully", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
