package bootcamp.taskmanager.application;

import bootcamp.taskmanager.application.dto.CreateTaskCommand;
import bootcamp.taskmanager.application.dto.TaskResponse;
import bootcamp.taskmanager.domain.Task;
import bootcamp.taskmanager.domain.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreateTaskUseCase {

    private final TaskRepository repository;

    public CreateTaskUseCase(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse execute(CreateTaskCommand command) {
        Optional<String> description = Optional.ofNullable(command.description());
        Task task = new Task(command.title(), description);
        Task saved = repository.save(task);
        return TaskResponse.fromDomain(saved);
    }
}
