package com.todolist.ToDoList.service;

import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUserId(UUID userId) {
        return taskRepository.findByUserId(userId);
    }

    public List<Task> getTasksByUserAndCategory(UUID userId, UUID categoryId) {
        return taskRepository.findByUserIdAndCategoryId(userId, categoryId);
    }

    public void deleteTask(UUID taskId) {
        taskRepository.deleteById(taskId);
    }
}
