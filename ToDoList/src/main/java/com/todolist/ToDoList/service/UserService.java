package com.todolist.ToDoList.service;

import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        // Check if a user with the same userSub already exists
        if (userRepository.existsByUserSub(user.getUserSub())) {
            throw new IllegalArgumentException("A user with this userSub already exists.");
        }

        // Save the user in the database
        return userRepository.save(user);
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /* Helper functions */

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User getUserBySub(String userSub) {
        return userRepository.findByUserSub(userSub);
    }
}
