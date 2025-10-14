package com.TaskManagement.TaskManagement.controller;

import java.net.URI;


import com.TaskManagement.TaskManagement.dto.request.PaginationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.entity.Priority;

import com.TaskManagement.TaskManagement.service.TaskService;

import org.springframework.data.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_TEAM_LEADER')")
    public ResponseEntity<Page<TaskResponse>> findAll(
            @Valid PaginationRequest paginationRequest) {
        log.info("Fetching tasks with pagination: page={}, size={}", paginationRequest.getPage(), paginationRequest.getSize());

        Page<TaskResponse> response = taskService.findAll(paginationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_TEAM_LEADER')")
    public ResponseEntity<TaskResponse> findById(@PathVariable Long id) {
        log.info("Fetching task with id: {}", id);

        return ResponseEntity.ok(taskService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<TaskResponse> createTask(@RequestBody @Valid TaskRequest taskRequest) {
        log.info("Creating task: {}", taskRequest);
        TaskResponse response = taskService.save(taskRequest);

        URI location = URI.create("/api/tasks/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<TaskResponse> update(
            @PathVariable Long id,
            @RequestBody @Valid TaskRequest taskRequest) {
        log.info("Updating task with id: {}", id);
        TaskResponse response = taskService.update(id, taskRequest);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        log.info("Deleting task with id: {}", id);
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_TEAM_LEADER')")
    public ResponseEntity<Page<TaskResponse>> search(
            @RequestParam String query,
            @Valid PaginationRequest paginationRequest) {
        log.info("Searching tasks with query: {}", query);
        Page<TaskResponse> response = taskService.search(query, paginationRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/completed")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_TEAM_LEADER')")
    public ResponseEntity<Page<TaskResponse>> findByCompleted(
            @RequestParam boolean completed,
            @Valid PaginationRequest paginationRequest) {
        log.info("Finding tasks with completed status: {}", completed);
        Page<TaskResponse> response = taskService.findByCompleted(completed, paginationRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/priority/{priority}")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_TEAM_LEADER')")
    public ResponseEntity<Page<TaskResponse>> findByPriority(
            @PathVariable Priority priority,
            @Valid PaginationRequest paginationRequest) {
        log.info("Finding tasks with priority: {}", priority);
        Page<TaskResponse> response = taskService.findByPriority(priority, paginationRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<TaskResponse> markAsCompleted(
            @PathVariable Long id,
            @RequestParam boolean completed) {
        log.info("Marking task {} as completed: {}", id, completed);

        return ResponseEntity.ok(taskService.markAsCompleted(id, completed));
    }

    @PutMapping("/{taskId}/assign/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<TaskResponse> assignTaskToUser(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        log.info("Assign task {} to user {}", taskId, userId);

        TaskResponse response = taskService.assignTaskToUser(taskId, userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}/unassign")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<Void> unassignTaskFromUser(@PathVariable Long taskId) {
        log.info("Unassigning task {}", taskId);

        taskService.unassignTaskFromUser(taskId);

        return ResponseEntity.noContent().build();
    }
}
