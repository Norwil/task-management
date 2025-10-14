package com.TaskManagement.TaskManagement.controller;

import com.TaskManagement.TaskManagement.dto.request.PaginationRequest;
import com.TaskManagement.TaskManagement.dto.request.RoleUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.TaskManagement.TaskManagement.dto.request.UserRequest;
import com.TaskManagement.TaskManagement.dto.response.UserResponse;

import com.TaskManagement.TaskManagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Valid PaginationRequest paginationRequest) {
        log.info("fetching users with pagination: page={}, size={}", paginationRequest.getPage(), paginationRequest.getSize());

        Page<UserResponse> response = userService.getAllUsers(paginationRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<UserResponse> findUserById(@PathVariable Long id) {
        log.info("Fetching user with id: {}", id);

        return ResponseEntity.ok(userService.findUserById(id));
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("Fetching user with username: " + username);

        return ResponseEntity.ok(userService.getUserByUsername(username));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserRequest userRequest) {
        log.info("Updating user with id: {}", id);
        UserResponse response = userService.updateUser(id, userRequest);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<UserResponse> updateRole(
            @PathVariable Long id,
            @RequestBody @Valid RoleUpdateRequest request) {
        log.info("Updating role of the user with id: " + id);

        return ResponseEntity.ok(userService.updateRole(id, request));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TEAM_LEADER')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
