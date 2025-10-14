package com.TaskManagement.TaskManagement.dto.response;

import com.TaskManagement.TaskManagement.entity.Priority;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    private Priority priority;
    private AssignedUserResponseDTO assignedUser;
}
