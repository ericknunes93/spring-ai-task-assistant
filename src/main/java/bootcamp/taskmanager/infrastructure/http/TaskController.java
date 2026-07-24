package bootcamp.taskmanager.infrastructure.http;

import bootcamp.taskmanager.application.CreateTaskUseCase;
import bootcamp.taskmanager.application.GetTaskSummaryUseCase;
import bootcamp.taskmanager.application.ListTasksUseCase;
import bootcamp.taskmanager.application.UpdateTaskStatusUseCase;
import bootcamp.taskmanager.application.dto.CreateTaskCommand;
import bootcamp.taskmanager.application.dto.TaskResponse;
import bootcamp.taskmanager.application.dto.TaskSummary;
import bootcamp.taskmanager.domain.TaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    private final GetTaskSummaryUseCase getTaskSummaryUseCase;

    public TaskController(CreateTaskUseCase createTaskUseCase,
                          ListTasksUseCase listTasksUseCase,
                          UpdateTaskStatusUseCase updateTaskStatusUseCase,
                          GetTaskSummaryUseCase getTaskSummaryUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.updateTaskStatusUseCase = updateTaskStatusUseCase;
        this.getTaskSummaryUseCase = getTaskSummaryUseCase;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@RequestBody CreateTaskCommand command) {
        TaskResponse response = createTaskUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> listTasks(@RequestParam(required = false) TaskStatus status) {
        List<TaskResponse> tasks = listTasksUseCase.execute(status);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable String id, @RequestParam TaskStatus status) {
        TaskResponse updated = updateTaskStatusUseCase.execute(id, status);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/summary")
    public ResponseEntity<TaskSummary> getSummary() {
        TaskSummary summary = getTaskSummaryUseCase.execute();
        return ResponseEntity.ok(summary);
    }
}
