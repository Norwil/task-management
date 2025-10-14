package com.TaskManagement.TaskManagement.mapper;

import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
import com.TaskManagement.TaskManagement.dto.response.AssignedUserResponseDTO;
import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.repository.TaskRepository;
import com.TaskManagement.TaskManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskMapper {
    private final UserRepository userRepository;


    /**
     * Convert TaskRequest -> Entity
     */
    public Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(LocalDateTime.parse(request.getDueDate()));        // Parse String to LocalDateTime
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found with id: " + request.getUserId()));
            task.setUser(user);
        }

        return task;
    }

    /**
     *  Convert Entity -> Response
     * @param task
     * @return TaskResponse
     */
    public TaskResponse toResponseDTO(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setPriority(task.getPriority());
        response.setDueDate(String.valueOf(task.getDueDate()));
        response.setCompleted(task.isCompleted());

        // Only set assigned user if task has a user
        if (task.getUser() != null) {
            AssignedUserResponseDTO assignedUser = new AssignedUserResponseDTO();
            assignedUser.setId(task.getUser().getId());
            assignedUser.setRole(task.getUser().getRole());
            assignedUser.setUsername(task.getUser().getUsername());
            response.setAssignedUser(assignedUser);
        }
        
        return response;
    }

    /**
     * Convert List<Task> -> List<Response>
     */
    public List<TaskResponse> toResponseList(List<Task> tasks) {
        return tasks.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}
