package dio.budgeting.infrastructure.repository;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTransactionRepositoryTest {

    private InMemoryTransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
    }

    @Test
    void shouldSaveAndFindAllTransactions() {
        Transaction t1 = new Transaction(null, new BigDecimal("100.00"), TransactionType.RECEITA, TransactionCategory.SALARIO, "Salário");
        Transaction t2 = new Transaction(null, new BigDecimal("45.00"), TransactionType.DESPESA, TransactionCategory.ALIMENTACAO, "Almoço");

        repository.save(t1);
        repository.save(t2);

        List<Transaction> transactions = repository.findAll();
        assertEquals(2, transactions.size());
    }

    @Test
    void shouldCalculateSumByTypeCorrectly() {
        Transaction t1 = new Transaction(null, new BigDecimal("5000.00"), TransactionType.RECEITA, TransactionCategory.SALARIO, "Salário");
        Transaction t2 = new Transaction(null, new BigDecimal("150.00"), TransactionType.DESPESA, TransactionCategory.MORADIA, "Conta de Luz");
        Transaction t3 = new Transaction(null, new BigDecimal("50.00"), TransactionType.DESPESA, TransactionCategory.ALIMENTACAO, "Jantar");

        repository.save(t1);
        repository.save(t2);
        repository.save(t3);

        BigDecimal totalReceitas = repository.sumByType(TransactionType.RECEITA);
        BigDecimal totalDespesas = repository.sumByType(TransactionType.DESPESA);

        assertEquals(new BigDecimal("5000.00"), totalReceitas);
        assertEquals(new BigDecimal("200.00"), totalDespesas);
    }
}
