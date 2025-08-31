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

    List<Task> findByPriority(Priority priority);
    List<Task> findByCompleted(boolean completed);
    Page<Task> findAll(Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Task> findByTitleOrDescriptionContainingIgnoreCase(@Param("query") String query);
}
