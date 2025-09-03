package com.TaskManagement.TaskManagement.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.service.TaskService;
import com.TaskManagement.TaskManagement.service.UserService;

import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private TaskService taskService;
    private UserService userService;
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    /**
     * Converts a Task entity to a TaskResponse DTO
     */
    private TaskResponse toTaskResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getDueDate().toString(),
            task.getPriority(),
            task.getUser() != null ? task.getUser().getId() : null
        );
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> findAll(
        @PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Fetching tasks with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Task> tasks = taskService.findAll(pageable);
        Page<TaskResponse> response = tasks.map(this::toTaskResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        log.info("Fetching task with id: {}", id);
        Task task = taskService.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id));
        return ResponseEntity.ok(toTaskResponse(task));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskRequest taskRequest) {
        log.info("Creating task: {}", taskRequest);
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        task.setDueDate(LocalDateTime.parse(taskRequest.getDueDate())); // Parse String to LocalDateTime
        if (taskRequest.getUserId() != null) {
            User user = userService.findById(taskRequest.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + taskRequest.getUserId()));
            task.setUser(user);
        }
        Task savedTask = taskService.save(task);
        URI location = URI.create("/api/tasks/" + savedTask.getId());
        return ResponseEntity.created(location).body(toTaskResponse(savedTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
        @PathVariable Long id,
        @RequestBody @Valid TaskRequest taskRequest) {
        log.info("Updating task with id: {}", id);
        Task task = taskService.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id));
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        task.setDueDate(LocalDateTime.parse(taskRequest.getDueDate()));
        if (taskRequest.getUserId() != null) {
            User user = userService.findById(taskRequest.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + taskRequest.getUserId()));
            task.setUser(user);
        }
        Task updatedTask = taskService.save(task);
        return ResponseEntity.ok(toTaskResponse(updatedTask));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("Deleting task with id: {}", id);
        taskService.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id));
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponse>> search(
        @RequestParam String query,
        @PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Searching tasks with query: {}", query);
        Page<Task> tasks = taskService.search(query, pageable);
        Page<TaskResponse> response = tasks.map(this::toTaskResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    public ResponseEntity<Page<TaskResponse>> findByCompleted(
        @RequestParam boolean completed,
        @PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Finding tasks with completed status: {}", completed);
        Page<Task> tasks = taskService.findByCompleted(completed, pageable);
        return ResponseEntity.ok(tasks.map(this::toTaskResponse));
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<Page<TaskResponse>> findByPriority(
        @PathVariable Priority priority,
        @PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Finding tasks with priority: {}", priority);
        Page<Task> tasks = taskService.findByPriority(priority, pageable);
        return ResponseEntity.ok(tasks.map(this::toTaskResponse));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> markAsCompleted(
        @PathVariable Long id,
        @RequestParam boolean completed) {
        log.info("Marking task {} as completed: {}", id, completed);
        Task task = taskService.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id));
            
        task.setCompleted(completed);
        Task updatedTask = taskService.save(task);
        return ResponseEntity.ok(toTaskResponse(updatedTask));
    }
}
