package com.TaskManagement.TaskManagement.service;

import com.TaskManagement.TaskManagement.dto.request.PaginationRequest;
import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Role;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.event.TaskAssignedEvent;
import com.TaskManagement.TaskManagement.mapper.TaskMapper;
import com.TaskManagement.TaskManagement.repository.TaskRepository;
import com.TaskManagement.TaskManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

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
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;
    private TaskRequest testTaskRequest;
    private TaskResponse testTaskResponse;
    private LocalDateTime testDueDate;

    @BeforeEach
    void setUp() {
        testDueDate = LocalDateTime.of(2026, 1, 1, 10, 0, 0);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.USER);
        testUser.setTasks(new ArrayList<>());

        testTask = new Task();
        testTask.setId(10L);
        testTask.setTitle("Initial Task");
        testTask.setDueDate(testDueDate);
        testTask.setPriority(Priority.HIGH);

        testTaskRequest = new TaskRequest();
        testTaskRequest.setTitle("New Task");
        testTaskRequest.setDueDate(testDueDate);
        testTaskRequest.setPriority(Priority.MEDIUM);

        testTaskResponse = new TaskResponse();
        testTaskResponse.setId(10L);
        testTaskResponse.setTitle("New Task");
        // AssignedUserResponseDTO setup would happen in TaskMapper mock
    }

    // -- SAVE METHOD TESTS --

    @Test
    void save_ShouldCreateAssignedTask_WhenUserIdIsValid() {
        // Arrange
        testTaskRequest.setUserId(1L);
        testTask.setUser(testUser);

        when(taskMapper.toEntity(testTaskRequest)).thenReturn(testTask);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponseDTO(testTask)).thenReturn(testTaskResponse);

        // Act
        TaskResponse result = taskService.save(testTaskRequest);

        // Assert
        assertNotNull(result, "Response should not be null");
        assertEquals(10L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(testTask);
        verify(eventPublisher, times(1)).publishEvent(any(TaskAssignedEvent.class));
    }

    @Test
    void save_ShouldCreateUnassignedTask_WhenUserIdIsNull() {
        // Arrange
        testTaskRequest.setUserId(null);
        testTask.setUser(null);

        when(taskMapper.toEntity(testTaskRequest)).thenReturn(testTask);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponseDTO(testTask)).thenReturn(testTaskResponse);

        // Act
        TaskResponse result = taskService.save(testTaskRequest);

        // Assert
        assertNotNull(result, "Response should not be null");
        verify(userRepository, never()).findById(anyLong());
        verify(taskRepository, times(1)).save(testTask);
        assertNull(testTask.getUser());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void save_ShouldThrowNoSuchElementException_WhenUserIdIsInvalid() {
        // Arrange
        testTaskRequest.setUserId(99L);

        when(taskMapper.toEntity(testTaskRequest)).thenReturn(testTask);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> taskService.save(testTaskRequest),
                "Should throw exception for non-existent User ID");
        verify(userRepository, times(1)).findById(99L);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // --- UPDATE METHOD TESTS ---

    @Test
    void updateShouldSuccessfullyReassignTask_WhenNEwUserIdIsValid() {
        // Arrange
        Long taskId = 10L;
        Long newUserId = 2L;
        User newUser = new User();
        newUser.setId(newUserId);

        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setUserId(newUserId);
        updateRequest.setTitle("Updated Title");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(newUserId)).thenReturn(Optional.of(newUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponseDTO(testTask)).thenReturn(testTaskResponse);

        // Act
        taskService.update(taskId, updateRequest);

        // Assert
        assertEquals("Updated Title", testTask.getTitle());
        assertEquals(newUserId, testTask.getUser().getId());
        verify(userRepository, times(1)).findById(newUserId);
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void update_ShouldSuccessfullyUnassignTask_WhenUserIdInRequestIsNull() {
        // Arrange
        Long taskId = 10L;
        testTask.setUser(testUser);

        TaskRequest updateRequest = new TaskRequest();
        updateRequest.setUserId(null);
        updateRequest.setTitle("Unassigned Title");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);
        when(taskMapper.toResponseDTO(testTask)).thenReturn(testTaskResponse);

        // Act
        taskService.update(taskId, updateRequest);

        // Assert
        assertEquals("Unassigned Title", testTask.getTitle());
        assertNull(testTask.getUser());
        verify(userRepository, never()).findById(anyLong());
        verify(taskRepository, times(1)).save(testTask);
    }

    // --- ASSIGN / UNASSIGN TESTS ---

    @Test
    void unassignTaskFromUser_ShouldSetUserToNull_WhenTaskIsAssigned() {
        // Arrange
        Long taskId = 10L;
        testTask.setUser(testUser);
        testUser.getTasks().add(testTask);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // Act
        taskService.unassignTaskFromUser(taskId);

        // Assert
        assertNull(testTask.getUser());
        assertFalse(testUser.getTasks().contains(testTask), "Task should be removed from User's collection");
        verify(taskRepository, times(1)).save(testTask);
    }

    @Test
    void unassignTaskFromUser_ShouldDoNothing_WhenTaskIsAlreadyUnassignedd() {
        // Arrange
        Long taskId = 10L;
        testTask.setUser(null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // Act
        taskService.unassignTaskFromUser(taskId);

        // Assert
        assertNull(testTask.getUser());
        verify(taskRepository, times(1)).save(testTask);
    }

    // --- PAGINATION / DEFENSIVE TESTS ---

    // TaskServiceTest.java (Corrected Test Method)

    @Test
    void findAll_ShouldThrowIllegalArgumentException_WhenOffsetIsTooLarge() {
        // --- Arrange ---

        PaginationRequest mockRequest = mock(PaginationRequest.class);

        Pageable overflowPageable = PageRequest.of(2, 2_147_483_647, Sort.by("id"));

        when(mockRequest.toPageable()).thenReturn(overflowPageable);

        // --- Act & Assert ---

        assertThrows(IllegalArgumentException.class,
                () -> taskService.findAll(mockRequest),
                "The service should intercept the oversized Pageable and throw an IllegalArgumentException.");

        verify(taskRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void search_ShouldPass_WhenQueryAndPaginationAreValid() {
        // Arrange
        PaginationRequest request = new PaginationRequest(0, 10, "id", Sort.Direction.ASC);
        Pageable  pageable = request.toPageable();

        List<Task> taskList = Collections.singletonList(testTask);
        Page<Task> taskPage = new PageImpl<>(taskList, pageable, 1);

        when(taskRepository.searchByTitleOrDescriptionContainingIgnoreCase(eq("test"), eq(pageable))).thenReturn(taskPage);
        when(taskMapper.toResponseDTO(any(Task.class))).thenReturn(testTaskResponse);

        // Act
        Page<TaskResponse> result = taskService.search("test", request);

        // Assert
        assertFalse(result.isEmpty());
        verify(taskRepository, times(1)).searchByTitleOrDescriptionContainingIgnoreCase(eq("test"), eq(pageable));
    }
    // --- DELETE TESTS ---

    @Test
    void deleteById_ShouldDeleteTask_WhenIdExists() {
        // Arrange
        Long taskId = 10L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(testTask));

        // Act
        taskService.deleteById(taskId);

        // Assert
        verify(taskRepository, times(1)).deleteById(taskId);
    }

    @Test
    void deleteById_ShouldThrowNoSuchElementException_WhenIdDoesNotExist() {
        // Arrange
        Long taskId = 99L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> taskService.deleteById(taskId));
        verify(taskRepository, never()).delete(any(Task.class));
    }

}
