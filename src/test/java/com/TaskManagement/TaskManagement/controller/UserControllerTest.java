package com.TaskManagement.TaskManagement.controller;


import com.TaskManagement.TaskManagement.dto.request.PaginationRequest;
import com.TaskManagement.TaskManagement.dto.request.RoleUpdateRequest;
import com.TaskManagement.TaskManagement.dto.request.UserRequest;
import com.TaskManagement.TaskManagement.dto.response.UserResponse;
import com.TaskManagement.TaskManagement.entity.Role;
import com.TaskManagement.TaskManagement.exception.UserNotFoundException;
import com.TaskManagement.TaskManagement.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.util.Collections;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(com.TaskManagement.TaskManagement.config.SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // Helper Objects
    private UserRequest validUserRequest;
    private UserResponse mockUserResponse;
    private RoleUpdateRequest roleUpdateRequest;

    @BeforeEach
    void setUp() {
        validUserRequest = new UserRequest(
                "newuser",
                "password",
                "new@example.com"
        );

        mockUserResponse = new UserResponse(
                2L,
                "newuser",
                "new@example.com",
                Role.USER,
                true,
                true,
                Collections.emptyList()
        );

        roleUpdateRequest = new RoleUpdateRequest(Role.TEAM_LEADER);
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- SECURITY TESTS (Access Control: Only TEAM_LEADER) ---

    @Test
    void getAllUsers_ShouldReturn401_WhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_ShouldReturn403_WhenUserLacksAuthority() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void deleteUser_ShouldReturn404_WhenUserDoesNotExist() throws Exception {
        // Arrange
        doThrow(new UserNotFoundException(99L)).when(userService).deleteUser(99L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    // --- FUNCITONAL TESTS (200/204) ---

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void getAllusers_ShouldReturn200AndPaginatedData() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username"));
        Page<UserResponse> mockPage = new PageImpl<>(Collections.singletonList(mockUserResponse), pageable, 1);

        // Mock
        when(userService.getAllUsers(any(PaginationRequest.class))).thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sortBy", "username")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("newuser"))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(userService, times(1)).getAllUsers(any(PaginationRequest.class));
    }

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void findUserById_ShouldReturn200AndUser() throws Exception {
        // Arrange
        when(userService.findUserById(2L)).thenReturn(mockUserResponse);

        // Act & Assert
        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void updateUser_ShouldReturn200AndUpdatedUser() throws Exception {
        // Arrange
        UserResponse updatedResponse = new UserResponse(2L, "updated_name", "new@example.com", Role.USER, true, true, Collections.emptyList());

        when(userService.updateUser(eq(2L), any(UserRequest.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(validUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated_name"));
    }

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void updateRole_ShouldReturn20AndUpdatedRole() throws Exception {
        // Arrange
        UserResponse leaderResponse = new UserResponse(2L, "newuser", "new@example.com", Role.TEAM_LEADER, true, true, Collections.emptyList());

        when(userService.updateRole(eq(2L), any(RoleUpdateRequest.class))).thenReturn(leaderResponse);

        // Act & Assert
        mockMvc.perform(put("/api/users/2/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(roleUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("TEAM_LEADER"));
    }

    @Test
    @WithMockUser(roles = "TEAM_LEADER")
    void deleteUser_ShouldReturn204_WhenUserIsDeleted() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(2L);

        // Act & Assert
        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isNoContent());
    }
}
