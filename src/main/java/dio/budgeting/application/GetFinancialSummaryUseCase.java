package dio.budgeting.application;

import dio.budgeting.application.dto.FinancialSummary;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

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
            BigDecimal receitas = Optional.ofNullable(repository.sumByType(TransactionType.RECEITA)).orElse(BigDecimal.ZERO);
            BigDecimal despesas = Optional.ofNullable(repository.sumByType(TransactionType.DESPESA)).orElse(BigDecimal.ZERO);
            BigDecimal saldo = receitas.subtract(despesas);
            return new FinancialSummary(receitas, despesas, saldo);
        }

        BigDecimal receitas = Optional.ofNullable(repository.sumByTypeAndCategory(TransactionType.RECEITA, category)).orElse(BigDecimal.ZERO);
        BigDecimal despesas = Optional.ofNullable(repository.sumByTypeAndCategory(TransactionType.DESPESA, category)).orElse(BigDecimal.ZERO);
        BigDecimal saldo = receitas.subtract(despesas);

        return new FinancialSummary(receitas, despesas, saldo);
    }
}
