package com.TaskManagement.TaskManagement.repository;


import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>  {

    /**
     * Finds all tasks with pagination and sorting
     */
    Page<Task> findAll(Pageable pageable);
    
    /**
     * Finds tasks by priority with pagination and sorting
     * @param priority the priority to filter by
     * @param pageable pagination and sorting parameters
     * @return page of tasks with the specified priority
     */
    Page<Task> findByPriority(Priority priority, Pageable pageable);
    
    /**
     * Finds tasks by completion status with pagination and sorting
     * @param completed the completion status to filter by
     * @param pageable pagination and sorting parameters
     * @return page of tasks with the specified completion status
     */
    Page<Task> findByCompleted(boolean completed, Pageable pageable);
    
    /**
     * Searches tasks by title or description containing the given query (case-insensitive)
     * with pagination and sorting
     * @param query the search query
     * @param pageable pagination and sorting parameters
     * @return page of tasks matching the search query
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Task> searchByTitleOrDescriptionContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    /**
     * Finds all tasks by user id with pagination and sorting
     * @param id the user id
     * @param pageable pagination and sorting parameters
     * @return page of tasks for the specified user
     */
    Page<Task> findByUserId(Long id, Pageable pageable);
    
    // Keep the non-paginated version for backward compatibility
    List<Task> findByUserId(Long id);
}
