package dio.budgeting.application;

import dio.budgeting.application.dto.TransactionResponse;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionRepository;
import dio.budgeting.domain.TransactionType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTransactionsUseCase {

    private final TransactionRepository repository;

    public ListTransactionsUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public List<TransactionResponse> execute(TransactionType type, TransactionCategory category) {
        if (type != null && category != null) {
            return repository.findByTypeAndCategory(type, category).stream()
                    .map(TransactionResponse::fromDomain)
                    .toList();
        }
        if (type != null) {
            return repository.findByType(type).stream()
                    .map(TransactionResponse::fromDomain)
                    .toList();
        }
        if (category != null) {
            return repository.findByCategory(category).stream()
                    .map(TransactionResponse::fromDomain)
                    .toList();
        }
        return repository.findAll().stream()
                .map(TransactionResponse::fromDomain)
                .toList();
    }
}
