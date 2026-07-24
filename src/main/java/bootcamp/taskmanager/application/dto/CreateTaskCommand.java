package bootcamp.taskmanager.application.dto;

public record CreateTaskCommand(
    String title,
    String description
) {}
