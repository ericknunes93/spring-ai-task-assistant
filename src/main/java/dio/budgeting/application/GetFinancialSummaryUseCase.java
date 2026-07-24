package dio.budgeting.application;

import dio.budgeting.application.dto.FinancialSummary;
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
        BigDecimal receitas = repository.sumByType(TransactionType.RECEITA);
        BigDecimal despesas = repository.sumByType(TransactionType.DESPESA);
        BigDecimal saldo = receitas.subtract(despesas);

        return new FinancialSummary(receitas, despesas, saldo);
    }
}
