package com.todolist.ToDoList.repository;

import com.todolist.ToDoList.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByUserId(UUID userId);
    Category findByUserIdAndId(UUID userId, UUID categoryId);
    Category findByUserIdAndTitle(UUID userId, String title);
    void deleteById(UUID categoryId);
}

