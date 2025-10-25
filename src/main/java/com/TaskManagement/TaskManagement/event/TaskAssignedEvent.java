package com.TaskManagement.TaskManagement.event;

public class TaskAssignedEvent {

    private final Long taskId;
    private final String taskTitle;
    private final Long userId;
    private final String userEmail;
    private final String username;

    public TaskAssignedEvent(Long taskId, String taskTitle, Long userId, String userEmail, String username) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.userId = userId;
        this.userEmail = userEmail;
        this.username = username;
    }

    public Long getTaskId() {
        return taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUsername() {
        return username;
    }
}
