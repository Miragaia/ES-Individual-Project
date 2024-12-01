package com.todolist.ToDoList.service;

import com.todolist.ToDoList.model.User;
import org.springframework.stereotype.Component;

@Component
public class AuthHandler {

    private final UserService userService;

    public AuthHandler(UserService userService) {
        this.userService = userService;
    }

    // Method to retrieve the authenticated user
    public User getAuthenticatedUser(String userSub) {

        User user = userService.getUserBySub(userSub);
        if (user == null) {
            return null;
        }

        System.out.println("UserAuthHandler" + user);
        return user;
    }
}
