package com.todolist.ToDoList.service;

import com.todolist.ToDoList.model.Task;
import com.todolist.ToDoList.model.User;
import com.todolist.ToDoList.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Task> getTasksByCategoryAndUser(UUID categoryId, User user) {
        return taskRepository.findByCategoryIdAndUser(categoryId, user);
    }
}
