package com.todolist.ToDoList.controller;

import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.DTO.CreateUserRequest;
import com.todolist.ToDoList.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication Controller", description = "Public API for managing user authentication")
public class AuthenticationController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }
    @Operation(summary = "Create a new user", description = "Creates a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest newUser) {
        logger.info("Attempting to create a new user");
        // Create a new user object with the data from the request
        User user = new User();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPassword(newUser.getPassword());

        // Check if the user is valid
        if (!userService.isUserValid(user)) {
            logger.error("Invalid user");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Check if the user already exists by its email
        if (userService.userExistsByEmail(user.getEmail())) {
            logger.error("User email already exists");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        User createdUser = userService.createUser(user);
        logger.info("User created successfully");
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    @Operation(summary = "Login a user", description = "Logs in a user to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@Parameter(description = "User email") @RequestParam String email,
            @Parameter(description = "User password") @RequestParam String password) {
        logger.info("Attempting to login a user");
        // Check if the user exists by its email
        if (!userService.userExistsByEmail(email)) {
            logger.error("User not found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Get the user by its email
        User user = userService.getUserByEmail(email);
        // Check if the password is correct
        if (!userService.isPasswordCorrect(password, user.getPassword())) {
            logger.error("Invalid password");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        logger.info("User logged in successfully");
        return new ResponseEntity<>(user, HttpStatus.OK); // TODO return a token
    }
}