package com.todolist.ToDoList.repository;

import com.todolist.ToDoList.model.Category;
import com.todolist.ToDoList.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);
    void deleteById(UUID categoryId);
    List<Category> findByUser(User user);
}

