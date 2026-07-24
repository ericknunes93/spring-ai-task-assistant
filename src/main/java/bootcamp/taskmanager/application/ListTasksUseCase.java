package bootcamp.taskmanager.application;

import bootcamp.taskmanager.application.dto.TaskResponse;
import bootcamp.taskmanager.domain.TaskRepository;
import bootcamp.taskmanager.domain.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTasksUseCase {

    private final TaskRepository repository;

    public ListTasksUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskResponse> execute(TaskStatus status) {
        if (status == null) {
            return repository.findAll().stream()
                    .map(TaskResponse::fromDomain)
                    .toList();
        }
        return repository.findByStatus(status).stream()
                .map(TaskResponse::fromDomain)
                .toList();
    }
}
