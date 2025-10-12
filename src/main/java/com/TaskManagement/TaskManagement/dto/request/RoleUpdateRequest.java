package com.TaskManagement.TaskManagement.dto.request;


import com.TaskManagement.TaskManagement.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RoleUpdateRequest {

    @NotNull(message = "Please provide the role of the user.")
    private Role role;

}
