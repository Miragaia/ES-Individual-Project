package com.todolist.ToDoList.service;

import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.repository.TaskRepository;

import com.todolist.ToDoList.model.Status;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

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

    @Transactional
    public void deleteTask(UUID taskId) {
        taskRepository.deleteTaskById(taskId);
    }

    public Task updateTask(Task existingTask, Task task) {
        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setDeadline(task.getDeadline());
        existingTask.setCategory(task.getCategory());
        existingTask.setStatus(task.getStatus());
        existingTask.setPriority(task.getPriority());
        return taskRepository.save(existingTask);
    }


    public List<Task> getTasksByStatusCategoryAndUser(Status status, UUID categoryId, User user, String sortBy) {
        return taskRepository.findByStatusAndCategoryIdAndUser(status, categoryId, user, Sort.by(Sort.Direction.ASC, sortBy));
    }
    
    public List<Task> getTasksByStatusAndUser(Status status, User user, String sortBy) {
        return taskRepository.findByStatusAndUser(status, user, Sort.by(Sort.Direction.ASC, sortBy));
    }
    
    public List<Task> getTasksByCategoryAndUser(UUID categoryId, User user, String sortBy) {
        return taskRepository.findByCategoryIdAndUser(categoryId, user, Sort.by(Sort.Direction.ASC, sortBy));
    }
    
    public List<Task> getTasksByUser(User user, String sortBy) {
        return taskRepository.findByUser(user, Sort.by(Sort.Direction.ASC, sortBy));
    }
}
    
