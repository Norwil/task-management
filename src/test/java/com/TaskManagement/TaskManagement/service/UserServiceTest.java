//package com.TaskManagement.TaskManagement.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import java.time.LocalDateTime;
//import java.util.*;
//import com.TaskManagement.TaskManagement.entity.*;
//import com.TaskManagement.TaskManagement.repository.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class UserServiceTest {
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private TaskRepository taskRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    private User user1;
//
//    @BeforeEach
//    void setUp() {
//        user1 = new User(1L, "testuser", "password", "test@example.com", Role.USER, true, true, new ArrayList<>(), LocalDateTime.now(), null);
//    }
//
//    @Test
//    void testSaveValidUser() {
//        // arrange
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
//        when(userRepository.save(user1)).thenReturn(user1);
//
//        // act
//        User result = userService.save(user1);
//
//        // assert
//        assertEquals(user1, result);
//        verify(userRepository).save(user1);
//    }
//
//    @Test
//    void testSaveDuplicateUsername() {
//        // arrange
//        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user1));
//
//        // act assert
//        assertThrows(IllegalArgumentException.class, () -> userService.save(user1));
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void testFindById() {
//        // arrange
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
//
//        // act
//        Optional<User> result = userService.findById(1L);
//
//        // assert
//        assertTrue(result.isPresent());
//        assertEquals(user1, result.get());
//        verify(userRepository).findById(1L);
//    }
//
//    @Test
//    void testFindByUserId() {
//        // arrange
//        Task task = new Task(1L, "Test Task", "Description", false, LocalDateTime.now(), Priority.HIGH, user1, LocalDateTime.now(), LocalDateTime.now());
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
//        when(taskRepository.findByUserId(1L)).thenReturn(List.of(task));
//
//        // act
//        List<Task> result = userService.findByUserId(1L);
//
//        // assert
//        assertEquals(1, result.size());
//        verify(taskRepository).findByUserId(1L);
//    }
//
//}
