package com.TaskManagement.TaskManagement.controller;

import com.TaskManagement.TaskManagement.dto.request.RoleUpdateRequest;
import com.TaskManagement.TaskManagement.dto.request.UserRequest;
import com.TaskManagement.TaskManagement.dto.response.UserResponse;
import com.TaskManagement.TaskManagement.entity.Role;
import com.TaskManagement.TaskManagement.exception.UserNotFoundException;
import com.TaskManagement.TaskManagement.service.UserService;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testGetAllUsers_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        UserResponse mockUserResponse = new UserResponse();
        mockUserResponse.setId(1L);
        mockUserResponse.setUsername("testuser");
        mockUserResponse.setEmail("test@example.com");

        List<UserResponse> userList = Collections.singletonList(mockUserResponse);
        Page<UserResponse> expectedPage = new PageImpl<>(userList, pageable, 1);

        when(userService.getAllUsers(pageable)).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<UserResponse>> responseEntity = userController.getAllUsers(pageable);

        // Assert & Verify
        verify(userService).getAllUsers(pageable);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value(), "HTTP status should be 200 OK");

        Page<UserResponse> actualPage = responseEntity.getBody();

        assertNotNull(actualPage, "Response body should not be null");
        assertEquals(1, actualPage.getTotalElements(), "The page should contain 1 element");
        assertEquals("testuser", actualPage.getContent().get(0).getUsername(), "The username should match");
    }

    @Test
    public void testFindUserByid_Success() {
        // Arrange
        Long userId = 42L;
        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setUsername("testuser");

        when(userService.findUserById(userId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> responseEntity = userController.findUserById(userId);

        // Assert & Verify
        verify(userService).findUserById(userId);
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals("testuser", responseEntity.getBody().getUsername());
    }

    @Test
    public void testGetUserByUsername_Success() {
        // Arrange
        String testUsername = "testuser_teamlead";
        Long expectedId = 99L;

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setUsername(testUsername);
        expectedResponse.setId(expectedId);
        expectedResponse.setEmail("test@user.com");

        when(userService.getUserByUsername(testUsername)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> responseEntity = userController.getUserByUsername(testUsername);

        // Assert & Verify
        verify(userService).getUserByUsername(testUsername);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value(), "HTTP status should be 200 OK");

        UserResponse actualResponse = responseEntity.getBody();
        assertNotNull(actualResponse, "Response body should not be null");
        assertEquals(testUsername, actualResponse.getUsername(), "The username in the response should match the input");
        assertEquals(expectedId, actualResponse.getId(), "The user ID in the response should match the expected ID");
    }

    @Test
    public void testUpdateUser_Success() {
        // Arrange
        Long userId = 100L;
        String newEmail = "updatedemail@test.com";
        String newUsername = "updatedusername";

        UserRequest request = new UserRequest();
        request.setEmail(newEmail);
        request.setUsername(newUsername);

        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setEmail(newEmail);
        response.setUsername(newUsername);

        when(userService.updateUser(userId, request)).thenReturn(response);

        // Act
        ResponseEntity<UserResponse> responseEntity = userController.updateUser(userId, request);

        // Assert & Verify
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value(), "HTTP status should be 200 OK");

        UserResponse actualResponse = responseEntity.getBody();
        assertNotNull(actualResponse, "Response body should not be null");
        assertEquals(userId, actualResponse.getId(), "The user ID must be preserved");
        assertEquals(newUsername, actualResponse.getUsername(), "The username should be the updated value");
        assertEquals(newEmail, actualResponse.getEmail(), "The email should be the the updated value");
    }

    @Test
    public void testUpdateRole_Success() {
        // Arrange
        Long userId = 200L;
        Role newRole = Role.USER;
        RoleUpdateRequest mockRequest = new RoleUpdateRequest();
        mockRequest.setRole(newRole);

        UserResponse expectedResponse = new UserResponse();
        expectedResponse.setId(userId);
        expectedResponse.setUsername("role_updater");
        expectedResponse.setRole(newRole);

        when(userService.updateRole(userId, mockRequest)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<UserResponse> responseEntity = userController.updateRole(userId, mockRequest);

        // Assert & Verify
        verify(userService).updateRole(userId, mockRequest);
        assertNotNull(responseEntity, "Response entity should not be null");
        assertEquals(200, responseEntity.getStatusCode().value(), "HTTP status should be 200 OK");

        UserResponse actualResponse = responseEntity.getBody();
        assertNotNull(actualResponse, "Response body should not be null");
        assertEquals(newRole, actualResponse.getRole(), "The role should be 'USER'");
        assertEquals(userId, actualResponse.getId(), "The user ID must be preserved");
    }

    @Test
    public void testDelete_Success() {
        // Arrange
        Long userId = 200L;
        doNothing().when(userService).deleteUser(userId);

        // Acct
        ResponseEntity<Void> responseEntity = userController.deleteUser(userId);

        // Assert & Verify
        verify(userService).deleteUser(userId);
        assertNotNull(responseEntity, "Response entity should not be null");
        assertEquals(204, responseEntity.getStatusCode().value(), "HTTP status should be 204 No Content");
        assertEquals(null, responseEntity.getBody(), "Response body must be null for 204 No Content");
    }

    @Test
    public void testFindUserById_NotFound() {
        // Arrange
        Long nonExistentUserId = 999L;
        UserNotFoundException mockException =
                new UserNotFoundException("User not found with ID: " + nonExistentUserId);
        when(userService.findUserById(nonExistentUserId))
                .thenThrow(mockException);

        // Act & Assert Exception
        UserNotFoundException thrown = assertThrows(
                UserNotFoundException.class,
                () -> userController.findUserById(nonExistentUserId),
                "Expected finduserById to throw UserNotFoundException"
        );

        assertEquals("User not found with ID: 999", thrown.getMessage(), "Exception message should match");
        verify(userService).findUserById(nonExistentUserId);
    }

    @Test
    public void testUpdateUser_InvalidData() {
        // Arrange
        Long userId = 400L;
        String invalidUser = "existing_user";

        UserRequest mockRequest = new UserRequest();
        mockRequest.setEmail("new@test.com");
        mockRequest.setUsername(invalidUser);

        NoSuchElementException mockException = new NoSuchElementException("Cannot find the user with id: " + userId);

        when(userService.updateUser(userId, mockRequest)).thenThrow(mockException);

        // Act & Assert
        NoSuchElementException thrown = assertThrows(
                NoSuchElementException.class,
                () -> userController.updateUser(userId, mockRequest),
                "Expected updateUser to throw NoSuchElementException"
        );

        assertEquals("Cannot find the user with id: " + userId, thrown.getMessage(), "Exception message should match");
        verify(userService).updateUser(userId, mockRequest);

    }
}