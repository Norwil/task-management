package com.TaskManagement.TaskManagement.controller;


import com.TaskManagement.TaskManagement.dto.request.PaginationRequest;
import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TaskController.class)
@Import(com.TaskManagement.TaskManagement.config.SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    // Helper Objects
    private final LocalDateTime testDate = LocalDateTime.of(2026, 1, 1, 12, 0, 0);
    private TaskRequest validRequest;
    private TaskResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Initialize DTOs in setup
        validRequest = new TaskRequest("Test Task", "Desc", Priority.HIGH, 1L, testDate);
        mockResponse = new TaskResponse(1L, "Test Task", "Desc", false, testDate, Priority.HIGH, null);
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- SECURITY TESTS (401/403) ---

    @Test
    void createTask_ShouldReturn401_WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/api/tasks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(validRequest)))
                .andExpect((status().isUnauthorized()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createTask_ShouldReturn403_WhenUserLacksAuthority() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRequest)))
                .andExpect(status().isForbidden());
    }

    // --- FUNCTIONAL & AUTHORIZATION TESTS (200/201) ---

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void createTask_ShouldReturn201_WhenAuthorized() throws Exception {
        // Arrange
        when(taskService.save(any(TaskRequest.class))).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Task"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAll_ShouldReturn200AndPaginateData_WhenAuthorized() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dueDate"));
        Page<TaskResponse> mockPage = new PageImpl<>(Collections.singletonList(mockResponse), pageable, 1);

        // Mock
        when(taskService.findAll(any(PaginationRequest.class))).thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "dueDate")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalPages").value(1));
    }
}
