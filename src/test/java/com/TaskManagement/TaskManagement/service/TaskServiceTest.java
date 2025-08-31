package com.TaskManagement.TaskManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService; 
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private Task task5;

    @BeforeEach
    void setUp() {
        task1 = new Task(1L, "Test Task 1", "Description", false, LocalDate.now().plusDays(1), Priority.MEDIUM);
        task2 = new Task(2L, "Test Task 2", "Description", true, LocalDate.now().plusDays(2), Priority.HIGH);
        task3 = new Task(3L, "Test Task 3", "Description", false, LocalDate.now().plusDays(3), Priority.LOW);
        task4 = new Task(4L, "Test Task 4", "Description", true, LocalDate.now().plusDays(4), Priority.MEDIUM);
        task5 = new Task(5L, "Test Task 5", "Description", false, LocalDate.now().plusDays(5), Priority.HIGH);
    }
    
    @Test
    void testFindAll() {
        // arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<Task> page = new PageImpl<>(List.of(task1, task2, task3, task4, task5), pageable, 5);
        when(taskRepository.findAll(pageable)).thenReturn(page);
        
        // act
        Page<Task> result = taskService.findAll(pageable);
        
        // assert
        assertEquals(5, result.getContent().size());
        assertEquals(task1, result.getContent().get(0));
        assertEquals(task2, result.getContent().get(1));
        assertEquals(task3, result.getContent().get(2));
        assertEquals(task4, result.getContent().get(3));
        assertEquals(task5, result.getContent().get(4));
        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindAllWithNullPageable() {
        // arrange
        assertThrows(IllegalArgumentException.class, () -> taskService.findAll(null));

        // act & assert
        verify(taskRepository, never()).findAll(any(Pageable.class));
    }
    
    @Test
    void testFindByIdFound() {
        // arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        // act
        Optional<Task> result = taskService.findById(1L);

        // assert
        assertTrue(result.isPresent());
        assertEquals(task1, result.get());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        // arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // act
        Optional<Task> result = taskService.findById(999L);

        // assert
        assertTrue(result.isEmpty());
        verify(taskRepository).findById(999L);
    }

    @Test
    void testSaveValidTask() {
        // arrange
        when(taskRepository.save(task1)).thenReturn(task1);

        // act
        Task result = taskService.save(task1);

        // assert
        assertEquals(task1, result);
        verify(taskRepository).save(task1);
    }

    @Test
    void testSaveNullTask() {
        // arrange act assert
        assertThrows(IllegalArgumentException.class, () -> taskService.save(null));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testSaveEmptyTitle() {
        // arrange
        Task invalidTask = new Task(null, "    ", null, false, null, Priority.HIGH);

        // act assert
        assertThrows(IllegalArgumentException.class, () -> taskService.save(invalidTask));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testDeleteByIdExisting() {
        // arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        // act
        taskService.deleteById(1L);

        // assert
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void testDeleteByIdNotFound() {
        // arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // act assert
        assertThrows(NoSuchElementException.class, () -> taskService.deleteById(999L));
        verify(taskRepository, never()).deleteById(999L);
    }

    @Test
    void testFindByCompleted() {
        // arrange
        when(taskRepository.findByCompleted(true)).thenReturn(List.of(task2, task4));

        // act
        List<Task> result = taskService.findByCompleted(true);

        // assert
        assertEquals(2, result.size());
        assertEquals(task2, result.get(0));
        assertEquals(task4, result.get(1));
        verify(taskRepository).findByCompleted(true);
    }

    @Test
    void testFindByPriority() {
        // arrange
        when(taskRepository.findByPriority(Priority.HIGH)).thenReturn(List.of(task2, task4));

        // act
        List<Task> result = taskService.findByPriority(Priority.HIGH);

        // assert
        assertEquals(2, result.size());
        assertEquals(task2, result.get(0));
        assertEquals(task4, result.get(1));
        verify(taskRepository).findByPriority(Priority.HIGH);
    }

    @Test
    void testFindByPriorityNull() {
        // arrange act assert
        assertThrows(IllegalArgumentException.class, () -> taskService.findByPriority(null));
        verify(taskRepository, never()).findByPriority(any(Priority.class));
    }

    @Test
    void testMarkAsCompleted() {
        // arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.save(any(Task.class))).thenReturn(task1);
        // act
        taskService.markAsCompleted(1L, true);

        // assert
        verify(taskRepository).save(task1);
    }

    @Test
    void testMarkAsCompletedNotFound() {
        // arrange act assert
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> taskService.markAsCompleted(999L, true));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void testSearchValidQuery() {
        // arrange
        when(taskRepository.findByTitleOrDescriptionContainingIgnoreCase("test")).thenReturn(List.of(task1, task2, task3, task4, task5));

        // act
        List<Task> result = taskService.search("test");

        // assert
        assertEquals(5, result.size());
        assertEquals(task1, result.get(0));
        assertEquals(task2, result.get(1));
        assertEquals(task3, result.get(2));
        assertEquals(task4, result.get(3));
        assertEquals(task5, result.get(4));
        verify(taskRepository).findByTitleOrDescriptionContainingIgnoreCase("test");
    }

    @Test
    void testSearchEmptyQuery() {
        // arrange act assert
        assertThrows(IllegalArgumentException.class, () -> taskService.search("     "));
        verify(taskRepository, never()).findByTitleOrDescriptionContainingIgnoreCase(anyString());
    }
}
