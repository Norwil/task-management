//package com.TaskManagement.TaskManagement.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.TaskManagement.TaskManagement.entity.Task;
//import com.TaskManagement.TaskManagement.dto.request.TaskRequest;
//import com.TaskManagement.TaskManagement.dto.request.UserRequest;
//import com.TaskManagement.TaskManagement.dto.response.TaskResponse;
//import com.TaskManagement.TaskManagement.dto.response.UserResponse;
//import com.TaskManagement.TaskManagement.entity.Priority;
//import com.TaskManagement.TaskManagement.entity.User;
//import com.TaskManagement.TaskManagement.entity.Role;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class TaskControllerIntegrationTest {
//
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//    private Task createTask(String title, Priority priority) {
//        return new Task(null, title, "Description", false, null, priority, null, null, null);
//    }
//
//    @Test
//    void testCreateTask() {
//        UserRequest userRequest = new UserRequest("testuser", "password123", "test@example.com", Role.USER);
//        restTemplate.postForEntity("/api/users", userRequest, UserResponse.class);
//        TaskRequest taskRequest = new TaskRequest("Test Task", "Description", Priority.HIGH, 1L, "2025-09-04T12:00:00");
//        ResponseEntity<TaskResponse> response = restTemplate.postForEntity("/api/tasks", taskRequest, TaskResponse.class);
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertEquals("Test Task", response.getBody().getTitle());
//        assertEquals(1L, response.getBody().getUserId());
//    }
//
//    @Test
//    void testGetAllTasksPaginated() {
//        // arrange act
//        ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks?page=0%size=2", String.class);
//
//        // assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().contains("content"));
//        assertTrue(response.getBody().contains("totalPages"));
//    }
//
//    @Test
//    void testGetTaskByIdFound() {
//        // arrange act
//        Task task = createTask("Test Task", Priority.HIGH);
//        ResponseEntity<Task> postResponse = restTemplate.postForEntity("/api/tasks", task, Task.class);
//        Long id = postResponse.getBody().getId();
//
//        // assert
//        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/" + id, Task.class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Test Task", response.getBody().getTitle());
//    }
//
//    @Test
//    void testGetTaskByIdNotFound() {
//        // arrange act assert
//        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/999", Task.class);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void testCreateTaskValid() {
//        // arrange
//        Task task = createTask("Test Task", Priority.HIGH);
//
//        // act
//        ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", task, Task.class);
//
//        // assert
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getHeaders().getLocation());
//        assertEquals("Test Task", response.getBody().getTitle());
//    }
//
//    @Test
//    void testCreateTaskInvalidTitle() {
//        // arrange act assert
//        Task task = createTask("", Priority.HIGH);
//        ResponseEntity<String> response = restTemplate.postForEntity("/api/tasks", task, String.class);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//
//    @Test
//    void testUpdateTaskValid() {
//        // arrange
//        Task task = createTask("Test Task", Priority.HIGH);
//        ResponseEntity<Task> postResponse = restTemplate.postForEntity("/api/tasks", task, Task.class);
//        // act
//        Long id = postResponse.getBody().getId();
//        Task updatedTask = createTask("Updated Task", Priority.LOW);
//        restTemplate.put("/api/tasks/" + id, updatedTask);
//        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/" + id, Task.class);
//        // assert
//        assertEquals("Updated Task", response.getBody().getTitle());
//    }
//
//    @Test
//    void testDeleteTaskValid() {
//        // arrange
//        Task task = createTask("Test task", Priority.HIGH);
//        ResponseEntity<Task> postResponse = restTemplate.postForEntity("/api/tasks", task, Task.class);
//        Long id = postResponse.getBody().getId();
//        // act
//        restTemplate.delete("/api/tasks/" + id);
//        // assert
//        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/" + id, Task.class);
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }
//
//    @Test
//    void testGetTasksByCompleted() {
//        // arrange act assert
//        ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks?completed=true", String.class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }
//
//    @Test
//    void testGetTasksByPriorityInvalid() {
//        // arrange act assert
//        ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks/priority/INVALID", String.class);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//    }
//
//    @Test
//    void testMarkAsCompleted() {
//        // arrange
//        Task task = createTask("Test Task", Priority.HIGH);
//        ResponseEntity<Task> postResponse = restTemplate.postForEntity("/api/tasks", task, Task.class);
//
//        // act
//        Long id = postResponse.getBody().getId();
//        String url = String.format("/api/tasks/mark-as-completed/%d?completed=true", id);
//        restTemplate.put(url, null);
//
//        // verify
//        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/" + id, Task.class);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().isCompleted());
//    }
//
//    @Test
//    void testSearchTasks() {
//        // arrange
//        Task task = createTask("Test Task", Priority.HIGH);
//        restTemplate.postForEntity("/api/tasks", task, Task.class);
//
//        // act
//        ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks/search?query=test", String.class);
//
//        // assert
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().contains("Test Task"));
//    }
//}
