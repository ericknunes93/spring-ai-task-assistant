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

import static org.junit.jupiter.api.Assertions.*;

class CreateTransactionUseCaseTest {

    private TransactionRepository repository;
    private CreateTransactionUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        useCase = new CreateTransactionUseCase(repository);
    }

    @Test
    void shouldCreateTransactionSuccessfully() {
        CreateTransactionCommand command = new CreateTransactionCommand(
                new BigDecimal("150.00"),
                TransactionType.DESPESA,
                TransactionCategory.ALIMENTACAO,
                "Almoço de negócios"
        );

        TransactionResponse response = useCase.execute(command);

        assertNotNull(response.id());
        assertEquals(new BigDecimal("150.00"), response.amount());
        assertEquals(TransactionType.DESPESA, response.type());
        assertEquals(TransactionCategory.ALIMENTACAO, response.category());
        assertEquals("Almoço de negócios", response.description());
    }
}
