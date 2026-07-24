package dio.budgeting.domain;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findAll();
    List<Transaction> findByType(TransactionType type);
    List<Transaction> findByCategory(TransactionCategory category);
    BigDecimal sumByType(TransactionType type);
}
