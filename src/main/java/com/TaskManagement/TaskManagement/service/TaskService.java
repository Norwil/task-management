package com.TaskManagement.TaskManagement.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.NoSuchElementException;

import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.mapper.TaskMapper;
import com.TaskManagement.TaskManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.repository.TaskRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    /**
     * Retrieves all tasks with pagination and sorting
     * @param pageable pagination and sorting parameters
     * @return a page of tasks
     * @throws IllegalArgumentException if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        Page<Task> page = taskRepository.findAll(pageable);
        Page<TaskResponse> response = page.map(taskMapper::toResponseDTO);
        return response;
    }

    /**
     * Retrieves tasks by title or description containing the query with pagination
     * @param query the query to search for
     * @param pageable pagination and sorting parameters
     * @return a page of tasks that match the query
     * @throws IllegalArgumentException if the query is null or empty, or if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> search(String query, Pageable pageable) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        Page<Task> page = taskRepository.searchByTitleOrDescriptionContainingIgnoreCase(query, pageable);
        Page<TaskResponse> response = page.map(taskMapper::toResponseDTO);
        return response;
    }

    /**
     * Retrieves a task by id
     * @param id the id of the task to retrieve
     * @return the task with the specified id
     */
    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find the task with id: " + id));

        TaskResponse response = taskMapper.toResponseDTO(task);

        return response;
    }

    /**
     * Saves a task to the database.
     * @param request the task to save
     * @return the saved task
     */
    public TaskResponse save(TaskRequest request) {
        Task tasktoSave = taskMapper.toEntity(request);
        Task savedTask = taskRepository.save(tasktoSave);
        TaskResponse response = taskMapper.toResponseDTO(savedTask);

        return response;
    }

    /**
     * Updates a task
     * @param request the task to save
     * @return the response
     */
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cannot find the user with id: " + id));
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(LocalDateTime.parse(request.getDueDate()));
        task.setPriority(request.getPriority());
        task.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NoSuchElementException("Cannot find the user with id: " + id)));

        Task savedTask = taskRepository.save(task);

        return taskMapper.toResponseDTO(savedTask);
    }

    /**
     * Deletes a task from database by id
     * @param id the id of the task to delete
     * @throws NoSuchElementException if the task is not found
     */
    public void deleteById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id));
        taskRepository.deleteById(id);
    }

    /**
     * Retrieves tasks by completion status with pagination
     * @param completed the completion status to filter by
     * @param pageable pagination and sorting parameters
     * @return a page of tasks with the specified completion status
     * @throws IllegalArgumentException if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> findByCompleted(boolean completed, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        Page<Task> page = taskRepository.findByCompleted(completed, pageable);
        Page<TaskResponse> response = page.map(taskMapper::toResponseDTO);
        return response;
    }
    
    /**
     * Retrieves tasks by priority with pagination
     * @param priority the priority to filter by
     * @param pageable pagination and sorting parameters
     * @return a page of tasks with the specified priority
     * @throws IllegalArgumentException if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> findByPriority(Priority priority, Pageable pageable) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null.");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }

        Page<Task> page = taskRepository.findByPriority(priority, pageable);
        Page<TaskResponse> response = page.map(taskMapper::toResponseDTO);

        return response;
    }

    /**
     * Mark a task as completed
     * @param id the id of the task to mark as completed
     * @param completed the completed status of the task to mark as completed
     */
    public TaskResponse markAsCompleted(Long id, boolean completed) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("task not found with id: " + id));

        task.setCompleted(completed);
        taskRepository.save(task);
        return taskMapper.toResponseDTO(task);
    }
}
