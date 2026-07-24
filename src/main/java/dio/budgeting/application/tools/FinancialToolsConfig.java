package dio.budgeting.application.tools;

import dio.budgeting.application.CreateTransactionUseCase;
import dio.budgeting.application.GetFinancialSummaryUseCase;
import dio.budgeting.application.ListTransactionsUseCase;
import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.FinancialSummary;
import dio.budgeting.application.dto.TransactionResponse;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Configuration
public class FinancialToolsConfig {

    public record CreateTransactionRequest(
        BigDecimal amount,
        String type,
        String category,
        String description
    ) {}

    public record ListTransactionsRequest(
        String type,
        String category
    ) {}

    public record GetSummaryRequest(
        String category
    ) {}

    @Bean
    @Description("Registra uma nova transação financeira de receita ou despesa. Exemplo: valor 45.0, tipo DESPESA, categoria ALIMENTACAO.")
    public Function<CreateTransactionRequest, TransactionResponse> criarTransacao(CreateTransactionUseCase useCase) {
        return request -> {
            TransactionType type = TransactionType.valueOf(request.type().toUpperCase());
            TransactionCategory category = request.category() != null ?
                    TransactionCategory.valueOf(request.category().toUpperCase()) : TransactionCategory.OUTROS;

            CreateTransactionCommand command = new CreateTransactionCommand(
                    request.amount(),
                    type,
                    category,
                    request.description()
            );
            return useCase.execute(command);
        };
    }

    @Bean
    @Description("Lista o histórico de transações financeiras registradas com filtro opcional por tipo e categoria.")
    public Function<ListTransactionsRequest, List<TransactionResponse>> listarTransacoes(ListTransactionsUseCase useCase) {
        return request -> {
            TransactionType type = request.type() != null && !request.type().isBlank() ? TransactionType.valueOf(request.type().toUpperCase()) : null;
            TransactionCategory category = request.category() != null && !request.category().isBlank() ? TransactionCategory.valueOf(request.category().toUpperCase()) : null;
            return useCase.execute(type, category);
        };
    }

    @Bean
    @Description("Obtém o resumo financeiro consolidado (total de receitas, despesas e saldo). Aceita filtro opcional de categoria (ex: ALIMENTACAO, TRANSPORTE, MORADIA, LAZER, SALARIO, OUTROS).")
    public Function<GetSummaryRequest, FinancialSummary> obterResumoFinanceiro(GetFinancialSummaryUseCase useCase) {
        return request -> {
            TransactionCategory category = request.category() != null && !request.category().isBlank() ?
                    TransactionCategory.valueOf(request.category().toUpperCase()) : null;
            return useCase.execute(category);
        };
    }
}
