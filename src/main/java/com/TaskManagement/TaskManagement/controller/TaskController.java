package com.TaskManagement.TaskManagement.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskService taskService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @GetMapping
    public ResponseEntity<Page<TaskResponse>> findAll(
        @PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Fetching tasks with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<TaskResponse> response = taskService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        log.info("Fetching task with id: {}", id);

        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskRequest taskRequest) {
        log.info("Creating task: {}", taskRequest);
        TaskResponse response = taskService.save(taskRequest);

        URI location = URI.create("/api/tasks/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> update(
        @PathVariable Long id,
        @RequestBody @Valid TaskRequest taskRequest) {
        log.info("Updating task with id: {}", id);
        TaskResponse response = taskService.update(id, taskRequest);

        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("Deleting task with id: {}", id);
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponse>> search(
        @RequestParam String query,
        @PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Searching tasks with query: {}", query);
        Page<TaskResponse> response = taskService.search(query, pageable);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    public ResponseEntity<Page<TaskResponse>> findByCompleted(
        @RequestParam boolean completed,
        @PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Finding tasks with completed status: {}", completed);
        Page<TaskResponse> response = taskService.findByCompleted(completed, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<Page<TaskResponse>> findByPriority(
        @PathVariable Priority priority,
        @PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Finding tasks with priority: {}", priority);
        Page<TaskResponse> response = taskService.findByPriority(priority, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> markAsCompleted(
        @PathVariable Long id,
        @RequestParam boolean completed) {
        log.info("Marking task {} as completed: {}", id, completed);

        return ResponseEntity.ok(taskService.markAsCompleted(id, completed));
    }
}
