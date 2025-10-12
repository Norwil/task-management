package com.TaskManagement.TaskManagement.mapper;

import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.request.UserRequest;
import com.TaskManagement.TaskManagement.dto.response.UserResponse;
import com.TaskManagement.TaskManagement.entity.Role;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final TaskMapper taskMapper;

    public User toEntity(UserRequest request) {
        if (request == null) return null;

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(Role.USER);
        return user;
    }

    public UserResponse toResponseDTO(User user) {
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setEnabled(user.isEnabled());
        response.setAccountNonLocked(user.isAccountNonLocked());
        response.setTasks(user.getTasks().stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList()));

        return response;
    }
}
