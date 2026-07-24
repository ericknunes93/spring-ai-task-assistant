package bootcamp.taskmanager.application.dto;

public record TaskSummary(
    long totalTasks,
    long pendingTasks,
    long inProgressTasks,
    long completedTasks
) {}
