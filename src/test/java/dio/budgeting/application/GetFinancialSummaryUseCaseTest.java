package dio.budgeting.application;

import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.FinancialSummary;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import dio.budgeting.infrastructure.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class GetFinancialSummaryUseCaseTest {

    private TransactionRepository repository;
    private CreateTransactionUseCase createUseCase;
    private GetFinancialSummaryUseCase summaryUseCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        createUseCase = new CreateTransactionUseCase(repository);
        summaryUseCase = new GetFinancialSummaryUseCase(repository);
    }

    @Test
    void shouldCalculateCorrectFinancialBalance() {
        createUseCase.execute(new CreateTransactionCommand(new BigDecimal("5000.00"), TransactionType.RECEITA, TransactionCategory.SALARIO, "Salário mensal"));
        createUseCase.execute(new CreateTransactionCommand(new BigDecimal("1200.00"), TransactionType.DESPESA, TransactionCategory.MORADIA, "Aluguel"));
        createUseCase.execute(new CreateTransactionCommand(new BigDecimal("300.00"), TransactionType.DESPESA, TransactionCategory.ALIMENTACAO, "Supermercado"));

        FinancialSummary summary = summaryUseCase.execute();

        assertEquals(new BigDecimal("5000.00"), summary.totalReceitas());
        assertEquals(new BigDecimal("1500.00"), summary.totalDespesas());
        assertEquals(new BigDecimal("3500.00"), summary.saldoAtual());
    }
}
