package com.TaskManagement.TaskManagement.service;


import com.TaskManagement.TaskManagement.dto.request.PaginationRequest;
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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PaginationRequest mockPaginationRequest;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserRequest testUserRequest;
    private UserResponse testUserResponse;
    private RoleUpdateRequest roleUpdateRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("rawPassword123");
        testUser.setRole(Role.USER);
        testUser.setTasks((new ArrayList<>()));

        testUserRequest = new UserRequest(
                "testuser",
                "rawPassword123",
                "test@example.com"
        );

        testUserResponse = new UserResponse(
                1L,
                "testuser",
                "test@example.com",
                Role.USER,
                true,
                true,
                Collections.emptyList()
        );

        roleUpdateRequest = new RoleUpdateRequest(Role.TEAM_LEADER);
    }

    // --- SECURITY CONTRACT (UserDetailsService) TESTS ---

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        // Act
        UserDetails result = userService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("nonexistent"));
    }

    // --- REGISTRATION & BASIC CRUD TESTS ---

    @Test
    void registerUser_ShouldHashPasswordAndSaveUser() {
        // Arrange
        when(userMapper.toEntity(testUserRequest)).thenReturn(testUser);
        when(passwordEncoder.encode("rawPassword123")).thenReturn("hashedPassword");
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toResponseDTO(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = userService.registerUser(testUserRequest);

        // Assert
        assertEquals(testUserResponse.getUsername(), result.getUsername());
        verify(passwordEncoder, times(1)).encode("rawPassword123");
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void findUserById_ShouldReturnUser_WhenIdExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponseDTO(testUser)).thenReturn(testUserResponse);

        // Act
        UserResponse result = userService.findUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findUserById_ShouldThrowUserNotFoundException_WhenIdDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.findUserById(99L));
    }

    // --- UPDATE TESTS ---

    @Test
    void updateUser_ShouldUpdateUsernameAndEmail() {
        // Arrange
        UserRequest updateRequest = new UserRequest(
                "new_username",
                "password_is_ignored",
                "new@email.com"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponseDTO(testUser)).thenReturn(testUserResponse);

        // Act
        userService.updateUser(1L, updateRequest);

        // Assert
        assertEquals("new_username", testUser.getUsername());
        assertEquals("new@email.com", testUser.getEmail());
        assertEquals("rawPassword123", testUser.getPassword());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void updateRole_ShouldChangeUserRole() {
        // Arrange
        User userBefore = testUser;
        User userAfter = testUser;
        userAfter.setRole(Role.TEAM_LEADER);

        when(userRepository.findById(1l)).thenReturn(Optional.of(userBefore));
        when(userRepository.save(userBefore)).thenReturn(userAfter);
        when(userMapper.toResponseDTO(userAfter)).thenReturn(testUserResponse);

        // Act
        userService.updateRole(1L, roleUpdateRequest);

        // Assert
        assertEquals(Role.TEAM_LEADER, userBefore.getRole());
        verify(userRepository, times(1)).save(userBefore);
    }

    // --- DELETE TESTS ---

    @Test
    void deleteUser_ShouldCallRepositoryDelete_WhenUserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    // --- PAGINATION TESTS ---

    @Test
    void getAllUsers_ShouldReturnPaginatedUsers_WhenRequestIsValid() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10, Sort.by("username"));

        Page<User> userPage = new PageImpl<>(Collections.singletonList(testUser), pageable, 1);

        when(mockPaginationRequest.toPageable()).thenReturn(pageable);
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toResponseDTO(any(User.class))).thenReturn(testUserResponse);

        // Act
        Page<UserResponse> result = userService.getAllUsers(mockPaginationRequest);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAll(pageable);

        verify(mockPaginationRequest, times(1)).toPageable();
    }

    @Test
    void getAllUser_ShouldThrowIllegalARgumentException_WhenOffsetIsTooLarge() {
        // Arrange
        Pageable overflowPageable = PageRequest.of(2, 2_147_483_647, Sort.by("id"));

        when(mockPaginationRequest.toPageable()).thenReturn(overflowPageable);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> userService.getAllUsers(mockPaginationRequest),
                "Should throw IllegalArgumentException for offset overflow"
        );

        verify(userRepository, never()).findAll(any(Pageable.class));
    }

}
