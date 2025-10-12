package com.TaskManagement.TaskManagement.dto.response;



import com.TaskManagement.TaskManagement.entity.Priority;

import com.TaskManagement.TaskManagement.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private String dueDate; // String for simpler JSON formatting
    private Priority priority;
    private AssignedUserResponseDTO assignedUser;
}
