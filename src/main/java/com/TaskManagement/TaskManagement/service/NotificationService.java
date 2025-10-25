package com.TaskManagement.TaskManagement.service;

import com.TaskManagement.TaskManagement.event.TaskAssignedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async
    @EventListener
    public void handleTaskAssignedEvent(TaskAssignedEvent event) {
        log.info("NOTIFICATION: Sending notification to user {} ({})",
                event.getUsername(),
                event.getUserEmail());

        log.info("NOTIFICATION: Notification sent for task {}.", event.getTaskId());
    }
}
