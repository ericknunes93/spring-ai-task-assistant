package bootcamp.taskmanager.application;

import bootcamp.taskmanager.application.dto.TaskResponse;
import bootcamp.taskmanager.domain.Task;
import bootcamp.taskmanager.domain.TaskId;
import bootcamp.taskmanager.domain.TaskRepository;
import bootcamp.taskmanager.domain.TaskStatus;
import org.springframework.stereotype.Service;

@Service
public class UpdateTaskStatusUseCase {

    private final TaskRepository repository;

    public UpdateTaskStatusUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse execute(String id, TaskStatus newStatus) {
        TaskId taskId = new TaskId(id);
        Task task = repository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Tarefa não encontrada com o ID: " + id));

        if (newStatus == TaskStatus.COMPLETED) {
            task.markAsCompleted();
        } else if (newStatus == TaskStatus.IN_PROGRESS) {
            task.markAsInProgress();
        } else {
            task.setStatus(newStatus);
        }

        repository.save(task);
        return TaskResponse.fromDomain(task);
    }
}
