package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.Category;
import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.todolist.ToDoList.service.AuthHandler;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final AuthHandler authHandler;

    public CategoryController(CategoryService categoryService, AuthHandler authHandler) {
        this.categoryService = categoryService;
        this.authHandler = authHandler;
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {

         // Get the currently authenticated user
        User user = authHandler.getAuthenticatedUser();
        System.out.println("User: " + user);
        
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        System.out.println("User ID for category: " + user.getId());


        category.setUser(user);

        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@RequestParam UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@RequestParam UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
