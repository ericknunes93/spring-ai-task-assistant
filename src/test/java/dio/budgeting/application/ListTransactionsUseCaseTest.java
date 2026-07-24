package dio.budgeting.application;

import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.TransactionResponse;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import dio.budgeting.infrastructure.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListTransactionsUseCaseTest {

    private TransactionRepository repository;
    private CreateTransactionUseCase createUseCase;
    private ListTransactionsUseCase listUseCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        createUseCase = new CreateTransactionUseCase(repository);
        listUseCase = new ListTransactionsUseCase(repository);

        createUseCase.execute(new CreateTransactionCommand(new BigDecimal("3000.00"), TransactionType.RECEITA, TransactionCategory.SALARIO, "Salário"));
        createUseCase.execute(new CreateTransactionCommand(new BigDecimal("100.00"), TransactionType.DESPESA, TransactionCategory.ALIMENTACAO, "Restaurante"));
        createUseCase.execute(new CreateTransactionCommand(new BigDecimal("50.00"), TransactionType.DESPESA, TransactionCategory.TRANSPORTE, "Uber"));
    }

    @Test
    void shouldFilterTransactionsByTypeAndCategory() {
        List<TransactionResponse> despesas = listUseCase.execute(TransactionType.DESPESA, null);
        assertEquals(2, despesas.size());

        List<TransactionResponse> transporte = listUseCase.execute(null, TransactionCategory.TRANSPORTE);
        assertEquals(1, transporte.size());
        assertEquals("Uber", transporte.get(0).description());

        List<TransactionResponse> receitaSalario = listUseCase.execute(TransactionType.RECEITA, TransactionCategory.SALARIO);
        assertEquals(1, receitaSalario.size());
    }
}
