package com.TaskManagement.TaskManagement.dto.request;

import com.TaskManagement.TaskManagement.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

}
