package com.TaskManagement.TaskManagement.service;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.repository.TaskRepository;

@Service
public class TaskService {

    
    private TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Retrieves all tasks with pagination and sorting
     * @param pageable pagination and sorting parameters
     * @return a page of tasks
     * @throws IllegalArgumentException if pageable is null
     */
    public Page<Task> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable cannot be null");
        }
        Page<Task> page = taskRepository.findAll(pageable);
        return page;
    }

    /**
     * Retrieves tasks by title or description containing the query
     * @param query the query to search for 
     * @return a list of tasks that match the query
     * @throw IllegalArgumentException if the query is null or empty
     */
    public List<Task> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be empty");
        }
        return taskRepository.findByTitleOrDescriptionContainingIgnoreCase(query);
    }

    /**
     * Retrieves a task by id
     * @param id the id of the task to retrieve
     * @return the task with the specified id
     */
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    /**
     * Saves a task to the database.
     * @param task the task to save
     * @return the saved task
     * @throws IllegalArgumentException if the task is null or the title is empty
     */
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        return taskRepository.save(task);
    }

    /**
     * Deletes a task from database by id
     * @param id the id of the task to delete
     * @throws NoSuchElementException if the task is not found
     */
    public void deleteById(Long id) {
        if (!taskRepository.findById(id).isPresent()) {
            throw new NoSuchElementException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    /**
     * Finds all tasks by completed status
     * @param completed the completed status of the tasks to find
     * @return a list of tasks with the specified completed status
     */
    public List<Task> findByCompleted(boolean completed) {
        return taskRepository.findByCompleted(completed);
    }
    
    /**
     * Finds all tasks by priority
     * @param priority the priority of the tasks to find
     * @return a list of tasks with the specified priority
     * @throws IllegalArgumentException if the priority is null
     */
    public List<Task> findByPriority(Priority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null.");
        }
        return taskRepository.findByPriority(priority);
    }

    /**
     * Mark a task as completed
     * @param id the id of the task to mark as completed
     * @param completed the completed status of the task to mark as completed
     */
    public void markAsCompleted(Long id, boolean completed) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (!taskOptional.isPresent()) {
            throw new NoSuchElementException("Task not found with id: " + id);
        }

        Task task = taskOptional.get();
        task.setCompleted(completed);
        taskRepository.save(task);
    }
}
