package dio.budgeting.application;

import dio.budgeting.application.dto.FinancialSummary;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class GetFinancialSummaryUseCase {

    private final TransactionRepository repository;

    public GetFinancialSummaryUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public FinancialSummary execute() {
        return execute(null);
    }

    public FinancialSummary execute(TransactionCategory category) {
        if (category == null) {
            BigDecimal receitas = repository.sumByType(TransactionType.RECEITA);
            BigDecimal despesas = repository.sumByType(TransactionType.DESPESA);
            BigDecimal saldo = receitas.subtract(despesas);
            return new FinancialSummary(receitas, despesas, saldo);
        }

        BigDecimal receitas = repository.findByTypeAndCategory(TransactionType.RECEITA, category).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal despesas = repository.findByTypeAndCategory(TransactionType.DESPESA, category).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = receitas.subtract(despesas);
        return new FinancialSummary(receitas, despesas, saldo);
    }
}
