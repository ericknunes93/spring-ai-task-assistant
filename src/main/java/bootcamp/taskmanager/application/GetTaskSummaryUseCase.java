package bootcamp.taskmanager.application;

import bootcamp.taskmanager.application.dto.TaskSummary;
import bootcamp.taskmanager.domain.TaskRepository;
import bootcamp.taskmanager.domain.TaskStatus;
import org.springframework.stereotype.Service;

@Service
public class GetTaskSummaryUseCase {

    private final TaskRepository repository;

    public GetTaskSummaryUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskSummary execute() {
        long pending = repository.countByStatus(TaskStatus.PENDING);
        long inProgress = repository.countByStatus(TaskStatus.IN_PROGRESS);
        long completed = repository.countByStatus(TaskStatus.COMPLETED);
        long total = repository.findAll().size();

        return new TaskSummary(total, pending, inProgress, completed);
    }
}
