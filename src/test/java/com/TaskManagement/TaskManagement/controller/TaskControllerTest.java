package com.TaskManagement.TaskManagement.controller;

import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.entity.Priority;

import com.TaskManagement.TaskManagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = Jackson2ObjectMapperBuilder.json()
                .modules(new JavaTimeModule()) // for LocalDateTime serialization
                .build();

        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new com.TaskManagement.TaskManagement.exception.GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper)) // 👈 key line
                .build();
    }


    @Test
    public void testFindById_Success() throws Exception {
        Long taskId = 1L;
        TaskResponse expectedResponse = new TaskResponse(
                taskId,
                "Test Title",
                "Test Description",
                false,
                "2025-10-15T10:00:00",
                Priority.MEDIUM,
                null
        );

        when(taskService.findById(taskId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void testCreateTask_Success() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New Task");
        request.setDescription("Task description");
        request.setPriority(Priority.HIGH);
        request.setDueDate("2025-10-20T10:00:00");
        request.setUserId(1L);

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setPriority(request.getPriority());
        response.setDueDate(request.getDueDate());

        when(taskService.save(any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void testCreateTask_InvalidRequest_ReturnsBadRequest() throws Exception {
        TaskRequest invalidRequest = new TaskRequest(); // missing required fields

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTask_Success() throws Exception {
        Long taskId = 1L;

        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Task");
        request.setDescription("Updated description");
        request.setPriority(Priority.MEDIUM);
        request.setDueDate("2025-11-01T10:00:00");
        request.setUserId(1L);

        TaskResponse response = new TaskResponse();
        response.setId(taskId);
        response.setTitle(request.getTitle());
        response.setDescription(request.getDescription());
        response.setPriority(request.getPriority());
        response.setDueDate(request.getDueDate());

        when(taskService.update(eq(taskId), any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void testUpdateTask_BAD() throws Exception {
        Long taskId = 99L;

        TaskRequest request = new TaskRequest();
        request.setTitle("Updated Task");
        request.setDescription("Updated description");
        request.setPriority(Priority.LOW);
        request.setDueDate("2025-11-01T10:00:00");
        request.setUserId(999L); // non-existent user

        String errorMessage = "Cannot find the user with id: " + taskId;

        when(taskService.update(eq(taskId), any(TaskRequest.class)))
                .thenThrow(new NoSuchElementException(errorMessage));

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof NoSuchElementException))
                .andExpect(result ->
                        assertEquals(errorMessage, result.getResolvedException().getMessage()));
    }

    @Test
    void testDeleteById_Success() throws Exception {
        // Arrange
        Long taskId = 1L;
        doNothing().when(taskService).deleteById(taskId);

        // Assert
        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteById_NotFound() throws Exception {
        Long taskId = 99L;
        doThrow(new NoSuchElementException("Task not found with id: " + taskId))
                .when(taskService).deleteById(taskId);

        mockMvc.perform(delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NoSuchElementException))
                .andExpect(result -> assertEquals("Task not found with id: " + taskId,
                        result.getResolvedException().getMessage()));
    }

    @Test
    void testSearchTasks_EmptyQuery_BadRequest() throws Exception {
        String query = "";

        when(taskService.search(eq(query), any()))
                .thenThrow(new IllegalArgumentException("Search query cannot be empty"));

        mockMvc.perform(get("/api/tasks/search")
                        .param("query", query))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("Search query cannot be empty",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void testFindByCompleted_InvalidPageable_BadRequest() throws Exception {
        boolean completed = true;

        when(taskService.findByCompleted(eq(completed), any()))
                .thenThrow(new IllegalArgumentException("Pageable cannot be null"));

        mockMvc.perform(get("/api/tasks/completed")
                        .param("completed", "true"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("Pageable cannot be null",
                        result.getResolvedException().getMessage()));
    }

    @Test
    void testUnassignTaskFromUser_Success() throws Exception {
        Long taskId = 1L;
        doNothing().when(taskService).unassignTaskFromUser(taskId);

        mockMvc.perform(put("/api/tasks/{taskId}/unassign", taskId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUnassignTaskFromUser_NotFound() throws Exception {
        Long taskId = 99L;
        doThrow(new NoSuchElementException("Task not found with id: " + taskId))
                .when(taskService).unassignTaskFromUser(taskId);

        mockMvc.perform(put("/api/tasks/{taskId}/unassign", taskId))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NoSuchElementException))
                .andExpect(result -> assertEquals("Task not found with id: " + taskId,
                        result.getResolvedException().getMessage()));
    }

}
