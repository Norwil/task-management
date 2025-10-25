package com.TaskManagement.TaskManagement.service;


import java.util.NoSuchElementException;

import com.TaskManagement.TaskManagement.dto.request.PaginationRequest;
import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.event.TaskAssignedEvent;
import com.TaskManagement.TaskManagement.mapper.TaskMapper;
import com.TaskManagement.TaskManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final ApplicationEventPublisher eventPublisher;

    private void validatePageableOffset(Pageable pageable) {
        if (pageable.getOffset() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Pagination offset is too large. Page (" + pageable.getPageNumber()
                    + ") times size (" + pageable.getPageSize()
                    + ") exceeds the maximum supported offset.");
        }
    }

    /**
     * Retrieves all tasks with pagination and sorting
     * @param request pageable pagination and sorting parameters
     * @return a page of tasks
     * @throws IllegalArgumentException if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> findAll(PaginationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Pagination request cannot be null");
        }
        Pageable pageable = request.toPageable();
        validatePageableOffset(pageable);

        Page<Task> page = taskRepository.findAll(pageable);
        Page<TaskResponse> response = page.map(taskMapper::toResponseDTO);
        return response;
    }

    /**
     * Retrieves tasks by title or description containing the query with pagination
     * @param query the query to search for
     * @param request pagination and sorting parameters
     * @return a page of tasks that match the query
     * @throws IllegalArgumentException if the query is null or empty, or if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> search(String query, PaginationRequest request) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        Pageable pageable = request.toPageable();
        validatePageableOffset(pageable);

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
     * @throws IllegalArgumentException when request is invalid
     * @throws NoSuchElementException when User ID is invalid
     */
    public TaskResponse save(TaskRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Task request cannot be null");
        }

        Task taskToSave = taskMapper.toEntity(request);
        User assignedUser = null;   // Kepe track of the user

        if (request.getUserId() != null) {
            assignedUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found with id: " + request.getUserId()));
            taskToSave.setUser(assignedUser);
        }

        Task savedTask = taskRepository.save(taskToSave);

        if (assignedUser != null) {
            publishTaskAssignedEvent(savedTask, assignedUser);
        }

        return taskMapper.toResponseDTO(savedTask);
    }

    /**
     * Updates a task
     * @param request the task to save
     * @return the response
     * @throws NoSuchElementException if the Task or the assigned User is not found.
     */
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + id)); // Corrected exception message

        if (request.getUserId() != null) {
            // New user ID provided, look up the user
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found with id: " + request.getUserId())); // Corrected exception message
            task.setUser(user);
        } else {
            // User ID is null in request: unassign the task
            task.setUser(null);
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate()); // Direct assignment from LocalDateTime DTO


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
     * @param request pagination and sorting parameters
     * @return a page of tasks with the specified completion status
     * @throws IllegalArgumentException if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> findByCompleted(boolean completed, PaginationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Pagination request cannot be null");
        }

        Pageable pageable = request.toPageable();
        validatePageableOffset(pageable);

        Page<Task> page = taskRepository.findByCompleted(completed, pageable);

        Page<TaskResponse> response = page.map(taskMapper::toResponseDTO);
        return response;
    }
    
    /**
     * Retrieves tasks by priority with pagination
     * @param priority the priority to filter by
     * @param request pagination and sorting parameters
     * @return a page of tasks with the specified priority
     * @throws IllegalArgumentException if pageable is null
     */
    @Transactional(readOnly = true)
    public Page<TaskResponse> findByPriority(Priority priority, PaginationRequest request) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null.");
        }
        if (request == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        Pageable pageable = request.toPageable();
        validatePageableOffset(pageable);

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

    /**
     * Assign a task to a user, notify
     * @param taskId
     * @param userId
     * @return the updated Task as a TaskResponse DTO
     * @throws NoSuchElementException if the Task or the User is not found.
     */
    @Transactional
    public TaskResponse assignTaskToUser(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));

        task.setUser(user);
        user.getTasks().add(task);

        Task savedTask = taskRepository.save(task);

        publishTaskAssignedEvent(savedTask, user);

        return taskMapper.toResponseDTO(savedTask);
    }

    /**
     * Unassign a task from the user
     * @param taskId
     * @throws NoSuchElementException if the Task is not found
     */
    @Transactional
    public void unassignTaskFromUser(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NoSuchElementException("Task not found with id: " + taskId));

        User assignedUser = task.getUser();

        if (assignedUser != null) {
            assignedUser.getTasks().remove(task);
            task.setUser(null);
        }

        taskRepository.save(task);
    }

    /**
     * Publish a task assigned event
     * @param task
     * @param user
     */
    private void publishTaskAssignedEvent(Task task, User user) {
        TaskAssignedEvent event = new TaskAssignedEvent(
                task.getId(),
                task.getTitle(),
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );

        log.info("Publishing TaskAssignedEvent for task {}", task.getId());
        eventPublisher.publishEvent(event);
    }
}
