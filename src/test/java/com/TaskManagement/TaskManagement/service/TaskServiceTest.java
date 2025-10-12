//package com.TaskManagement.TaskManagement.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//
//import org.junit.jupiter.api.*;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.*;
//
//import com.TaskManagement.TaskManagement.entity.*;
//import com.TaskManagement.TaskManagement.repository.*;
//
//@ExtendWith(MockitoExtension.class)
//public class TaskServiceTest {
//
//    @Mock
//    private TaskRepository taskRepository;
//
//    @InjectMocks
//    private TaskService taskService;
//    private Task task1;
//    private Task task2;
//    private Task task3;
//    private Task task4;
//    private Task task5;
//
//    @BeforeEach
//    void setUp() {
//        User user = new User(1L, "testuser", "password", "test@example.com", Role.USER, true, true, new ArrayList<>(), LocalDateTime.now(), null);
//        LocalDateTime now = LocalDateTime.now();
//        task1 = new Task(1L, "Test Task 1", "Description", false, now.plusDays(1), Priority.MEDIUM, user, now, now);
//        task2 = new Task(2L, "Test Task 2", "Description", true, now.plusDays(2), Priority.HIGH, user, now, now);
//        task3 = new Task(3L, "Test Task 3", "Description", false, now.plusDays(3), Priority.LOW, user, now, now);
//        task4 = new Task(4L, "Test Task 4", "Description", true, now.plusDays(4), Priority.MEDIUM, user, now, now);
//        task5 = new Task(5L, "Test Task 5", "Description", false, now.plusDays(5), Priority.HIGH, user, now, now);
//    }
//
//    @Test
//    void testFindAll() {
//        // arrange
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Task> page = new PageImpl<>(List.of(task1, task2, task3, task4, task5), pageable, 5);
//        when(taskRepository.findAll(pageable)).thenReturn(page);
//
//        // act
//        Page<Task> result = taskService.findAll(pageable);
//
//        // assert
//        assertEquals(5, result.getContent().size());
//        assertEquals(task1, result.getContent().get(0));
//        assertEquals(task2, result.getContent().get(1));
//        assertEquals(task3, result.getContent().get(2));
//        assertEquals(task4, result.getContent().get(3));
//        assertEquals(task5, result.getContent().get(4));
//        verify(taskRepository, times(1)).findAll(pageable);
//    }
//
//    @Test
//    void testFindAllWithNullPageable() {
//        // arrange
//        assertThrows(IllegalArgumentException.class, () -> taskService.findAll(null));
//
//        // act & assert
//        verify(taskRepository, never()).findAll(any(Pageable.class));
//    }
//
//    @Test
//    void testFindByIdFound() {
//        // arrange
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
//
//        // act
//        Optional<Task> result = taskService.findById(1L);
//
//        // assert
//        assertTrue(result.isPresent());
//        assertEquals(task1, result.get());
//        verify(taskRepository, times(1)).findById(1L);
//    }
//
//    @Test
//    void testFindByIdNotFound() {
//        // arrange
//        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // act
//        Optional<Task> result = taskService.findById(999L);
//
//        // assert
//        assertTrue(result.isEmpty());
//        verify(taskRepository).findById(999L);
//    }
//
//    @Test
//    void testSaveValidTask() {
//        // arrange
//        when(taskRepository.save(task1)).thenReturn(task1);
//
//        // act
//        Task result = taskService.save(task1);
//
//        // assert
//        assertEquals(task1, result);
//        verify(taskRepository).save(task1);
//    }
//
//    @Test
//    void testSaveNullTask() {
//        // arrange act assert
//        assertThrows(IllegalArgumentException.class, () -> taskService.save(null));
//        verify(taskRepository, never()).save(any(Task.class));
//    }
//
//    @Test
//    void testSaveEmptyTitle() {
//        // arrange
//        Task invalidTask = new Task(null, "    ", null, false, null, Priority.HIGH, null, null, null);
//
//        // act assert
//        assertThrows(IllegalArgumentException.class, () -> taskService.save(invalidTask));
//        verify(taskRepository, never()).save(any(Task.class));
//    }
//
//    @Test
//    void testDeleteByIdExisting() {
//        // arrange
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
//
//        // act
//        taskService.deleteById(1L);
//
//        // assert
//        verify(taskRepository).deleteById(1L);
//    }
//
//    @Test
//    void testDeleteByIdNotFound() {
//        // arrange
//        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // act assert
//        assertThrows(NoSuchElementException.class, () -> taskService.deleteById(999L));
//        verify(taskRepository, never()).deleteById(999L);
//    }
//
//    @Test
//    void testFindByCompleted() {
//        // arrange
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Task> page = new PageImpl<>(List.of(task2, task4));
//        when(taskRepository.findByCompleted(true, pageable)).thenReturn(page);
//
//        // act
//        Page<Task> result = taskService.findByCompleted(true, pageable);
//
//        // assert
//        assertEquals(2, result.getContent().size());
//        assertEquals(task2, result.getContent().get(0));
//        assertEquals(task4, result.getContent().get(1));
//        verify(taskRepository).findByCompleted(true, pageable);
//    }
//
//    @Test
//    void testFindByPriority() {
//        // arrange
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Task> page = new PageImpl<>(List.of(task2, task5));
//        when(taskRepository.findByPriority(Priority.HIGH, pageable)).thenReturn(page);
//
//        // act
//        Page<Task> result = taskService.findByPriority(Priority.HIGH, pageable);
//
//        // assert
//        assertEquals(2, result.getContent().size());
//        assertEquals(task2, result.getContent().get(0));
//        assertEquals(task5, result.getContent().get(1));
//        verify(taskRepository).findByPriority(Priority.HIGH, pageable);
//    }
//
//    @Test
//    void testFindByPriorityNull() {
//        // arrange
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // act assert
//        assertThrows(IllegalArgumentException.class,
//            () -> taskService.findByPriority(null, pageable));
//        verify(taskRepository, never()).findByPriority(any(), any(Pageable.class));
//    }
//
//    @Test
//    void testMarkAsCompleted() {
//        // arrange
//        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
//        when(taskRepository.save(any(Task.class))).thenReturn(task1);
//        // act
//        taskService.markAsCompleted(1L, true);
//
//        // assert
//        verify(taskRepository).save(task1);
//    }
//
//    @Test
//    void testMarkAsCompletedNotFound() {
//        // arrange act assert
//        when(taskRepository.findById(999L)).thenReturn(Optional.empty());
//        assertThrows(NoSuchElementException.class, () -> taskService.markAsCompleted(999L, true));
//        verify(taskRepository, never()).save(any(Task.class));
//    }
//
//    @Test
//    void testSearchValidQuery() {
//        // arrange
//        String query = "test";
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Task> page = new PageImpl<>(List.of(task1, task2, task3, task4, task5));
//        when(taskRepository.searchByTitleOrDescriptionContainingIgnoreCase(query, pageable)).thenReturn(page);
//
//        // act
//        Page<Task> result = taskService.search(query, pageable);
//
//        // assert
//        assertEquals(5, result.getContent().size());
//        assertEquals(task1, result.getContent().get(0));
//        assertEquals(task2, result.getContent().get(1));
//        assertEquals(task3, result.getContent().get(2));
//        assertEquals(task4, result.getContent().get(3));
//        assertEquals(task5, result.getContent().get(4));
//        verify(taskRepository).searchByTitleOrDescriptionContainingIgnoreCase(query, pageable);
//    }
//
//    @Test
//    void testSearchEmptyQuery() {
//        // arrange
//        Pageable pageable = PageRequest.of(0, 10);
//
//        // act assert
//        assertThrows(IllegalArgumentException.class,
//            () -> taskService.search("     ", pageable));
//        verify(taskRepository, never()).searchByTitleOrDescriptionContainingIgnoreCase(anyString(), any(Pageable.class));
//    }
//}
