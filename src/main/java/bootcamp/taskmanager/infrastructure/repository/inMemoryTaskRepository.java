package bootcamp.taskmanager.infrastructure.repository;

import bootcamp.taskmanager.domain.Task;
import bootcamp.taskmanager.domain.TaskId;
import bootcamp.taskmanager.domain.TaskRepository;
import bootcamp.taskmanager.domain.TaskStatus;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<TaskId, Task> storage = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        storage.put(task.getId(), task);
        return task;
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Task> findById(TaskId id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Task> findByStatus(TaskStatus status) {
        return storage.values().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(TaskId id) {
        storage.remove(id);
    }

    @Override
    public long countByStatus(TaskStatus status) {
        return storage.values().stream()
                .filter(t -> t.getStatus() == status)
                .count();
    }
}
