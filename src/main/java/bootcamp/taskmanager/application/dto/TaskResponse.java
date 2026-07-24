package bootcamp.taskmanager.application.dto;

import bootcamp.taskmanager.domain.Task;
import bootcamp.taskmanager.domain.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
    String id,
    String title,
    String description,
    TaskStatus status,
    LocalDateTime createdAt
) {
    public static TaskResponse fromDomain(Task task) {
        return new TaskResponse(
            task.getId().id().toString(),
            task.getTitle(),
            task.getDescription().orElse(null),
            task.getStatus(),
            task.getCreatedAt()
        );
    }
}
