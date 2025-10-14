package com.TaskManagement.TaskManagement.config;

import com.TaskManagement.TaskManagement.entity.Priority;
import com.TaskManagement.TaskManagement.entity.Role;
import com.TaskManagement.TaskManagement.entity.Task;
import com.TaskManagement.TaskManagement.entity.User;
import com.TaskManagement.TaskManagement.repository.TaskRepository;
import com.TaskManagement.TaskManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder; // Injected to hash passwords

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {

            User admin = createUser("admin", "admin@example.com", "admin123", Role.TEAM_LEADER);

            User user1 = createUser("john_doe", "john@example.com", "user123", Role.USER);
            User user2 = createUser("jane_smith", "jane@example.com", "user123", Role.USER);

            List<User> users = userRepository.saveAll(Arrays.asList(admin, user1, user2));

            createSampleTasks(users);

            System.out.println("Sample data initialized successfully! Users: admin/admin123, john_doe/user123.");
        }
    }

    private User createUser(String username, String email, String rawPassword, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);

        return user;
    }

    private void createSampleTasks(List<User> users) {
        LocalDateTime now = LocalDateTime.now();

        // Tasks for admin (User ID 1, assuming H2 starts ID at 1)
        User admin = users.get(0);
        Task task1 = createTask("Review project proposal", "Review and provide feedback on the new project proposal",
                now.plusDays(2), Priority.HIGH, admin);

        // Tasks for user1
        User john = users.get(1);
        Task task3 = createTask("Implement authentication", "Implement JWT authentication for the API",
                now.plusDays(3), Priority.HIGH, john);

        Task task4 = createTask("Write unit tests", "Write unit tests for user service",
                now.plusDays(2), Priority.MEDIUM, john);

        // Unassigned task
        Task task7 = createTask("Backlog Item", "Needs triage and assignment.",
                now.plusDays(7), Priority.LOW, null);

        // Save all tasks
        taskRepository.saveAll(Arrays.asList(task1, task3, task4, task7));
    }

    private Task createTask(String title, String description, LocalDateTime dueDate, Priority priority, User user) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setDueDate(dueDate);
        task.setPriority(priority);
        task.setUser(user);
        task.setCompleted(false);
        return task;
    }
}
