package bootcamp.taskmanager.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
public class Task {
    private TaskId id;
    private String title;
    private Optional<String> description;
    private TaskStatus status;
    private LocalDateTime createdAt;

    public Task(String title, Optional<String> description) {
        Assert.notNull(title, "Title must not be null");
        Assert.hasText(title, "Title must not be empty");

        this.id = new TaskId();
        this.title = title;
        this.description = description != null ? description : Optional.empty();
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
    }

    public void markAsInProgress() {
        this.status = TaskStatus.IN_PROGRESS;
    }
}
