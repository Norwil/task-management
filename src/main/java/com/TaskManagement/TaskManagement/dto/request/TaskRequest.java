package com.TaskManagement.TaskManagement.dto.request;

import com.TaskManagement.TaskManagement.entity.Priority;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskRequest {
    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot be longer than 100 characters")
    private String title;

    private String description;

    @NotNull(message = "Priority cannot be null")
    private Priority priority;

    private Long userId;

    @NotNull(message = "Due date cannot be null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dueDate;  // String for JSON input, parse to LocalDateTime
}
