package com.todolist.ToDoList.repository;

import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByUserId(UUID userId);
    List<Task> findByUserIdAndCategoryId(UUID userId, UUID categoryId);
    List<Task> findByUserIdAndStatus(UUID userId, String status);
    List<Task> findByUserIdAndPriority(UUID userId, String priority);
    List<Task> findByUserIdAndStatusAndPriority(UUID userId, String status, String priority);
    List<Task> findByUserIdAndCategoryIdAndStatus(UUID userId, UUID categoryId, String status);
    List<Task> findByUserIdAndCategoryIdAndPriority(UUID userId, UUID categoryId, String priority);
    void deleteTaskById(UUID taskId);
    List<Task> findByCategoryIdAndUser(UUID categoryId, User user);
    List<Task> findByStatusAndUser(String status, User user);
    
}

