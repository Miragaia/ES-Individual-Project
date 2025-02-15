package com.todolist.ToDoList.repository;

import com.todolist.ToDoList.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    User findByEmail(String email);
    User findByUserSub(String sub);
    Boolean existsByUserSub(String userSub);
}
