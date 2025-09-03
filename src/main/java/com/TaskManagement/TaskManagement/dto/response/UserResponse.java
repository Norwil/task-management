package com.TaskManagement.TaskManagement.dto.response;

import com.TaskManagement.TaskManagement.entity.Role;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;
    private boolean accountNonLocked;
    private List<TaskResponse> tasks;
}