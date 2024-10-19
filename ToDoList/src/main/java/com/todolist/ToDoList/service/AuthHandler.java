package com.todolist.ToDoList.service;

import com.todolist.ToDoList.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthHandler {

    private final UserService userService;

    public AuthHandler(UserService userService) {
        this.userService = userService;
    }

    // Method to retrieve the authenticated user
    public User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userService.findByUsername(username);  // Fetch the User entity by username
        }

        return null;
    }
}
