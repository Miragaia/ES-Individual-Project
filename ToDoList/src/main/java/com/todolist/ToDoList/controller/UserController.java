package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.service.UserService;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.apache.el.stream.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@RequestParam UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("email/{email}")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        logger.info("Attempting to get user by an email");
        if (!userService.isEmailValid(email)) {
            logger.error("Invalid email");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = userService.getUserByEmail(email);
        if (user == null) {
            logger.info("No user found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        logger.info("User found");
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
