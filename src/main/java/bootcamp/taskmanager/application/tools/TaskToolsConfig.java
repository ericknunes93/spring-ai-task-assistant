package bootcamp.taskmanager.application.tools;

import bootcamp.taskmanager.application.CreateTaskUseCase;
import bootcamp.taskmanager.application.GetTaskSummaryUseCase;
import bootcamp.taskmanager.application.ListTasksUseCase;
import bootcamp.taskmanager.application.UpdateTaskStatusUseCase;
import bootcamp.taskmanager.application.dto.CreateTaskCommand;
import bootcamp.taskmanager.application.dto.TaskResponse;
import bootcamp.taskmanager.application.dto.TaskSummary;
import bootcamp.taskmanager.domain.TaskStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.function.Function;

@Configuration
public class TaskToolsConfig {

    public record CreateTaskRequest(String titulo, String descricao) {}
    public record ListTasksRequest(String filter) {}
    public record UpdateTaskStatusRequest(String id, String novoStatus) {}
    public record GetSummaryRequest(String unused) {}

    @Bean
    @Description("Cria uma nova tarefa com título e descrição opcional.")
    public Function<CreateTaskRequest, TaskResponse> criarTarefa(CreateTaskUseCase useCase) {
        return request -> useCase.execute(new CreateTaskCommand(request.titulo(), request.descricao()));
    }

    @Bean
    @Description("Lista todas as tarefas cadastradas no sistema.")
    public Function<ListTasksRequest, List<TaskResponse>> listarTarefas(ListTasksUseCase useCase) {
        return request -> useCase.execute(null);
    }

    @Bean
    @Description("Atualiza o status de uma tarefa existente para PENDING, IN_PROGRESS ou COMPLETED.")
    public Function<UpdateTaskStatusRequest, TaskResponse> atualizarStatusTarefa(UpdateTaskStatusUseCase useCase) {
        return request -> {
            TaskStatus status = TaskStatus.valueOf(request.novoStatus().toUpperCase());
            return useCase.execute(request.id(), status);
        };
    }

    @Bean
    @Description("Obtém o resumo estatístico contendo o total de tarefas, pendentes, em progresso e concluídas.")
    public Function<GetSummaryRequest, TaskSummary> obterResumoTarefas(GetTaskSummaryUseCase useCase) {
        return request -> useCase.execute();
    }
}
