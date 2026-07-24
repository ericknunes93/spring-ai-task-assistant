package bootcamp.taskmanager.domain;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    Task save(Task task);
    List<Task> findAll();
    Optional<Task> findById(TaskId id);
    List<Task> findByStatus(TaskStatus status);
    void delete(TaskId id);
    long countByStatus(TaskStatus status);
}
