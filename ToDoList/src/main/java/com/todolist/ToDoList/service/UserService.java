package com.todolist.ToDoList.service;

import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.repository.UserRepository;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private int MIN_USERNAME_LENGTH = 5;
    private int MAX_USERNAME_LENGTH = 255;
    private int MIN_EMAIL_LENGTH = 5;
    private int MAX_EMAIL_LENGTH = 100;
    private int MIN_PASSWORD_LENGTH = 8;
    private int MAX_PASSWORD_LENGTH = 80;
    private String EMAIL_REGEX = "[a-z][a-z0-9._+-]+@[a-z]+\\.[a-z]{2,6}";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /* Helper functions */
    public boolean isUserValid(User user) {
        String username = user.getUsername();
        String email = user.getEmail();
        String password = user.getPassword();
        if (username == null || username.length() < MIN_USERNAME_LENGTH || username.length() > MAX_USERNAME_LENGTH) {
            System.out.println("Username is invalid");
            return false;
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            System.out.println("Password is invalid!" + password + " " + password.length());
            return false;
        }
        return isEmailValid(email);
    }
    public boolean isEmailValid(String email) {
        if (email == null) {
            System.out.println("Email is null");
            return false;
        }
        return (email.length() > MIN_EMAIL_LENGTH && email.length() < MAX_EMAIL_LENGTH && email.matches(EMAIL_REGEX));
    }
    public boolean isPasswordCorrect(String password, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(password, encodedPassword);
    }

    public boolean userExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
