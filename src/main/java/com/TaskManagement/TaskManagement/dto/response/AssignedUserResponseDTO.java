package com.TaskManagement.TaskManagement.dto.response;


import com.TaskManagement.TaskManagement.entity.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AssignedUserResponseDTO {

    private Long id;
    private String username;
    private Role role;

}
