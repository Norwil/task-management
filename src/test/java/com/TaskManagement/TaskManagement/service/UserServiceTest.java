package com.TaskManagement.TaskManagement.service;

import com.TaskManagement.TaskManagement.dto.request.RoleUpdateRequest;
import com.TaskManagement.TaskManagement.dto.request.UserRequest;
import com.TaskManagement.TaskManagement.dto.response.UserResponse;
import com.TaskManagement.TaskManagement.entity.Role;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.exception.UserNotFoundException;
import com.TaskManagement.TaskManagement.mapper.UserMapper;
import com.TaskManagement.TaskManagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User testUser;
    private UserRequest testUserRequest;
    private UserResponse testUserResponse;
    private final Long TEST_USER_ID = 1L;
    private final String TEST_USERNAME = "testuser";
    private final String TEST_EMAIL = "test@example.com";
    private final String TEST_PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encodedPassword123";

    private User userToSave;
    private User userAfterSave;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPassword(ENCODED_PASSWORD);
        testUser.setRole(Role.USER);

        testUserRequest = new UserRequest();
        testUserRequest.setUsername(TEST_USERNAME);
        testUserRequest.setEmail(TEST_EMAIL);
        testUserRequest.setPassword(TEST_PASSWORD);

        testUserResponse = new UserResponse();
        testUserResponse.setId(TEST_USER_ID);
        testUserResponse.setUsername(TEST_USERNAME);
        testUserResponse.setEmail(TEST_EMAIL);
        testUserResponse.setRole(Role.USER);

        userToSave = new User();
        userToSave.setUsername(TEST_USERNAME);
        userToSave.setEmail(TEST_EMAIL);
        userToSave.setPassword(TEST_PASSWORD);
        userToSave.setRole(Role.USER);

        userAfterSave = new User();
        userAfterSave.setId(TEST_USER_ID);
        userAfterSave.setUsername(TEST_USERNAME);
        userAfterSave.setEmail(TEST_EMAIL);
        userAfterSave.setPassword(ENCODED_PASSWORD);
        userAfterSave.setRole(Role.USER);

    }

    @Test
    void loadUserByUsername_WhenUserExists_ReturnsUserDetails() {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // Act
        var result = userService.loadUserByUsername(TEST_USERNAME);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
    }

    @Test
    void loadUserByUsername_WhenUserNotExists_ThrowException() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Asser
        assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername("nonexistent")
        );
    }

    @Test
    void getUserByUsername_WhenUserExists_ReturnsuserResponse() {
        // Arrange
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDTO(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse response = userService.getUserByUsername(TEST_USERNAME);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_USERNAME, response.getUsername());
        verify(userRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void getAllusers_ReturnsPageOfUsers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(testUserResponse);

        // Act
        Page<UserResponse> result = userService.getAllUsers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void findUserById_WhenUserNotExists_ThrowsException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act && Assert
        assertThrows(UserNotFoundException.class, () ->
                userService.findUserById(999L)
        );
    }

    @Test
    void updateUser_WithValidData_ReturnsUpdatedUser() {
        // Arrange
        UserRequest updateRequest = new UserRequest();
        updateRequest.setUsername("updateduser");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPassword("newpassword");

        User updatedUser = new User();
        updatedUser.setId(TEST_USER_ID);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setPassword(ENCODED_PASSWORD);

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(TEST_USER_ID);
        updatedResponse.setUsername("updateduser");
        updatedResponse.setEmail("updated@example.com");

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(updatedResponse);

        // Act
        UserResponse response = userService.updateUser(TEST_USER_ID, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("updateduser", response.getUsername());
        assertEquals("updated@example.com", response.getEmail());
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("updateduser", savedUser.getUsername());
    }

    @Test
    void updateRole_WithValidData_UpdatesUserRole() {
        // Arrange
        RoleUpdateRequest roleUpdateRequest = new RoleUpdateRequest();
        roleUpdateRequest.setRole(Role.TEAM_LEADER);

        User adminUser = new User();
        adminUser.setId(TEST_USER_ID);
        adminUser.setUsername(TEST_USERNAME);
        adminUser.setEmail(TEST_EMAIL);
        adminUser.setRole(Role.TEAM_LEADER);

        UserResponse adminResponse = new UserResponse();
        adminResponse.setId(TEST_USER_ID);
        adminResponse.setRole(Role.TEAM_LEADER);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(adminResponse);

        // Act
        UserResponse response = userService.updateRole(TEST_USER_ID, roleUpdateRequest);

        // Assert
        assertNotNull(response);
        assertEquals(Role.TEAM_LEADER, response.getRole());
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(Role.TEAM_LEADER, savedUser.getRole());
    }

    @Test
    void deleteUser_WhenUser_exists_DeleteUser() {
        // Arrange
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).deleteById(TEST_USER_ID);

        // Act
        userService.deleteUser(TEST_USER_ID);

        // Assert
        verify(userRepository).findById(TEST_USER_ID);
        verify(userRepository).deleteById(TEST_USER_ID);
    }

    @Test
    void deleteUser_WhenUserNotExists_ThrowsException() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () ->
                userService.deleteUser(999L)
        );
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void registerUser_WithValidRequest_ReturnUserResponse() {
        // Arrange
        when(passwordEncoder.encode(TEST_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userMapper.toEntity(testUserRequest)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(userAfterSave);
        when(userMapper.toResponseDTO(userAfterSave)).thenReturn(testUserResponse);

        // Act
        UserResponse response = userService.registerUser(testUserRequest);

        // Assert
        assertNotNull(response);
        assertEquals(TEST_USERNAME, response.getUsername());
        verify(passwordEncoder).encode(TEST_PASSWORD);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals(ENCODED_PASSWORD, capturedUser.getPassword());
    }
}