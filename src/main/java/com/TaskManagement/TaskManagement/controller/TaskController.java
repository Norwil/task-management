package com.TaskManagement.TaskManagement.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.service.TaskService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private TaskService taskService;
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<Page<Task>> findAll(@PageableDefault(page = 0, size = 10, sort = "dueDate", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Fetching tasks with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Task> tasks = taskService.findAll(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        Optional<Task> task = taskService.findById(id);
        if (task.isPresent()) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build(); // status 404
        }
    }

    @PostMapping
    public ResponseEntity<Task> save(@RequestBody @Valid Task task) {
        log.info("Creating task: {}", task);
        Task savedTask = taskService.save(task);
        URI location = URI.create("/api/tasks/" + savedTask.getId());
        return ResponseEntity.created(location).body(savedTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> update(@PathVariable Long id, @RequestBody @Valid Task task) {
        Optional<Task> taskOptional = taskService.findById(id);
        if (!taskOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        task.setId(id);
        Task updatedTask = taskService.save(task);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        Optional<Task> taskOptional = taskService.findById(id);
        if (!taskOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        taskService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> search(@RequestParam String query) {
        log.info("Searching tasks with query: {}", query);
        return ResponseEntity.ok(taskService.search(query));
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Task>> findByCompleted(@RequestParam boolean completed) {
        List<Task> tasks = taskService.findByCompleted(completed);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> findByPriority(@PathVariable Priority priority) {
        List<Task> tasks = taskService.findByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/mark-as-completed/{id}")
    public ResponseEntity<Task> markAsCompleted(@PathVariable Long id, @RequestParam boolean completed) {
        Optional<Task> taskOptional = taskService.findById(id);
        if (!taskOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        taskService.markAsCompleted(id, completed);
        return ResponseEntity.ok(taskService.findById(id).get());
    }
}
