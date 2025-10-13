package com.TaskManagement.TaskManagement.service;

import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.AssignedUserResponseDTO;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Role;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.mapper.TaskMapper;
import com.TaskManagement.TaskManagement.repository.TaskRepository;
import com.TaskManagement.TaskManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Captor
    ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);


    private User testUser;
    private final String TEST_DUE_DATE_STRING = "2025-10-30T10:00:00";  // Sample date string
    private final Long TEST_TASK_ID = 1L;
    private Task testTask;
    private TaskResponse testTaskResponse;
    private User newUser;

    @BeforeEach
    void setUp() {

        // Setup the Test User entity
        testUser = new User();
        testUser.setId(2L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@email.com");
        testUser.setRole(Role.USER);

        newUser = new User();
        newUser.setId(3L);
        newUser.setUsername("newuser");

        // Initialize the entity the mock will return
        testTask = new Task();
        testTask.setId(TEST_TASK_ID);
        testTask.setTitle("Test Title");
        testTask.setPriority(Priority.HIGH);
        testTask.setUser(testUser);
        testTask.setDueDate(LocalDateTime.parse(TEST_DUE_DATE_STRING));

        // Initialize the expected TaskResponse
        testTaskResponse = new TaskResponse();
        testTaskResponse.setId(TEST_TASK_ID);
        testTaskResponse.setTitle("Test Title");
        testTaskResponse.setDescription("testdescription");
        testTaskResponse.setPriority(Priority.HIGH);
        testTaskResponse.setDueDate(TEST_DUE_DATE_STRING);

        AssignedUserResponseDTO assignedUser = new AssignedUserResponseDTO();
        assignedUser.setId(testUser.getId());
        assignedUser.setUsername(testUser.getUsername());
        assignedUser.setRole(testUser.getRole());

        testTaskResponse.setAssignedUser(assignedUser);
    }

    @Test
    void findById_WhenTaskExists_ReturnTaskResponse() {
        // Arrange
        when(taskRepository.findById(TEST_TASK_ID)).thenReturn(Optional.of(testTask));
        when(taskMapper.toResponseDTO(testTask)).thenReturn(testTaskResponse);

        // Act
        TaskResponse response = taskService.findById(TEST_TASK_ID);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TASK_ID, response.getId());
        verify(taskRepository).findById(TEST_TASK_ID);
    }

    @Test
    void findById_WhenTaskNoExists_ThrowsException() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                taskService.findById(999L)
        );
    }

    @Test
    void saveTask_WithValidData_ReturnsTask() {
        // Arrange
        TaskRequest createTask = new TaskRequest();
        createTask.setTitle("Test Title");
        createTask.setDescription("testdescription");
        createTask.setPriority(Priority.HIGH);
        createTask.setUserId(testUser.getId());
        createTask.setDueDate(TEST_DUE_DATE_STRING);

        Task taskEntityAfterMapping = new Task();
        taskEntityAfterMapping.setTitle("Test Title");
        taskEntityAfterMapping.setDescription("testdescription");
        taskEntityAfterMapping.setPriority(Priority.HIGH);
        taskEntityAfterMapping.setUser(testUser);
        taskEntityAfterMapping.setDueDate(LocalDateTime.parse(TEST_DUE_DATE_STRING));

        Task savedTaskWithId = new Task();
        savedTaskWithId.setId(TEST_TASK_ID);
        savedTaskWithId.setTitle("Test Title");
        savedTaskWithId.setDescription("testdescription");
        savedTaskWithId.setPriority(Priority.HIGH);
        savedTaskWithId.setUser(testUser);
        savedTaskWithId.setDueDate(LocalDateTime.parse(TEST_DUE_DATE_STRING));

        // Mock userRepository to return testUser
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        when(taskMapper.toEntity(createTask)).thenReturn(taskEntityAfterMapping);
        when(taskRepository.save(taskEntityAfterMapping)).thenReturn(savedTaskWithId);
        when(taskMapper.toResponseDTO(savedTaskWithId)).thenReturn(testTaskResponse);

        // Act
        TaskResponse response = taskService.save(createTask);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_TASK_ID, response.getId());
        assertEquals("Test Title", response.getTitle());
        assertEquals("testdescription", response.getDescription());
        assertEquals(Priority.HIGH, response.getPriority());

        // Verify interactions
        verify(userRepository).findById(testUser.getId());
        verify(taskMapper).toEntity(createTask);
        verify(taskRepository).save(taskEntityAfterMapping);
        verify(taskMapper).toResponseDTO(savedTaskWithId);
    }

    @Test
    void deleteById_WhenTaskExists_DeletesTask() {
        // Arrange
        when(taskRepository.findById(TEST_TASK_ID)).thenReturn(Optional.of(testTask));
        doNothing().when(taskRepository).deleteById(TEST_TASK_ID);

        // Act
        taskService.deleteById(TEST_TASK_ID);

        // Assert
        verify(taskRepository).findById(TEST_TASK_ID);
        verify(taskRepository).deleteById(TEST_TASK_ID);
    }

    @Test
    void deleteById_WhenTaskNotExists_ThrowsException() {
        // Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                taskService.deleteById(999L)
        );
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void markAsCompleted_WhenTaskExists_UpdatesStatusAndReturnsResponse() {
        // Arrange
        when(taskRepository.findById(TEST_TASK_ID)).thenReturn(Optional.of(testTask));
        when(taskMapper.toResponseDTO(testTask)).thenReturn(testTaskResponse);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        // Act
        TaskResponse result = taskService.markAsCompleted(TEST_TASK_ID, true);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TASK_ID, result.getId());

        verify(taskRepository).save(taskCaptor.capture());

        Task savedTask = taskCaptor.getValue();

        assertTrue(savedTask.isCompleted());

        verify(taskRepository).findById(TEST_TASK_ID);
        verify(taskMapper).toResponseDTO(savedTask);
    }

    @Test
    void update_WithValidData_ReturnsUpdatedTaskResponse() {
        // Arrange
        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setTitle("update title");
        updateRequest.setDescription("update description");
        updateRequest.setPriority(Priority.MEDIUM);
        updateRequest.setUserId(newUser.getId());
        updateRequest.setDueDate(TEST_DUE_DATE_STRING);

        when(taskRepository.findById(TEST_TASK_ID)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(newUser.getId())).thenReturn(Optional.of(newUser));

        // Make save() return the updated task instead of null
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Make the mapper return an updated response
        testTaskResponse.setDescription("update description");
        testTaskResponse.setTitle("update title");
        testTaskResponse.setPriority(Priority.MEDIUM);
        when(taskMapper.toResponseDTO(any(Task.class))).thenReturn(testTaskResponse);

        // Act
        TaskResponse result = taskService.update(TEST_TASK_ID, updateRequest);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(taskCaptor.capture());
        Task savedTask = taskCaptor.getValue();

        assertEquals("update title", savedTask.getTitle());
        assertEquals(newUser.getId(), savedTask.getUser().getId());
        assertEquals("update description", result.getDescription());
        assertEquals(Priority.MEDIUM, result.getPriority());
    }

    @Test
    void saveTask_WhenAssignedUserNotFound_ThrowException() {
        // Arrange
        TaskRequest requestWithBadUser = new TaskRequest();
        requestWithBadUser.setTitle("testtitle");
        requestWithBadUser.setDescription("testdescription");
        requestWithBadUser.setPriority(Priority.HIGH);
        requestWithBadUser.setDueDate(TEST_DUE_DATE_STRING);
        requestWithBadUser.setUserId(999L);

        // Mock the user lookup failure
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException thrown = assertThrows(NoSuchElementException.class, () ->
                taskService.save(requestWithBadUser)
        );

        assertEquals("User not found with id: 999", thrown.getMessage());
        verify(userRepository).findById(999L);
        verifyNoInteractions(taskMapper);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void update_WhenTaskNotExists_ThrowException() {
        // Arrange
        TaskRequest dummyRequest = new TaskRequest();
        dummyRequest.setTitle("dummy title");
        dummyRequest.setDescription("dummy description");
        dummyRequest.setDueDate(TEST_DUE_DATE_STRING);
        dummyRequest.setUserId(testUser.getId());
        dummyRequest.setPriority(Priority.MEDIUM);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () ->
                taskService.update(TEST_TASK_ID, dummyRequest)
        );
        verify(taskRepository, never()).save(any(Task.class));
    }
}
