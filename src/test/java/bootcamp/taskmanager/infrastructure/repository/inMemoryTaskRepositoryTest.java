package bootcamp.taskmanager.infrastructure.repository;

import bootcamp.taskmanager.domain.Task;
import bootcamp.taskmanager.domain.TaskStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {

    private final InMemoryTaskRepository repository = new InMemoryTaskRepository();

    @Test
    void shouldSaveAndFindTask() {
        Task task = new Task("Testar repositório", Optional.of("Descrição de teste"));
        repository.save(task);

        Optional<Task> found = repository.findById(task.getId());
        assertTrue(found.isPresent());
        assertEquals("Testar repositório", found.get().getTitle());
        assertEquals(TaskStatus.PENDING, found.get().getStatus());
    }

    @Test
    void shouldFilterByStatus() {
        Task task1 = new Task("Tarefa 1", Optional.empty());
        Task task2 = new Task("Tarefa 2", Optional.empty());
        task2.markAsCompleted();

        repository.save(task1);
        repository.save(task2);

        List<Task> pendingTasks = repository.findByStatus(TaskStatus.PENDING);
        List<Task> completedTasks = repository.findByStatus(TaskStatus.COMPLETED);

        assertEquals(1, pendingTasks.size());
        assertEquals(1, completedTasks.size());
    }
}
