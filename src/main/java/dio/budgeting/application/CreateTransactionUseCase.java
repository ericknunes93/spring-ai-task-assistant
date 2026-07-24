package dio.budgeting.application;

import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.TransactionResponse;
import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateTransactionUseCase {

    private final TransactionRepository repository;

    public CreateTransactionUseCase(TransactionRepository repository) {
        this.repository = repository;
    }

    public TransactionResponse execute(CreateTransactionCommand command) {
        Transaction transaction = new Transaction(
            null,
            command.amount(),
            command.type(),
            command.category(),
            command.description()
        );
        Transaction saved = repository.save(transaction);
        return TransactionResponse.fromDomain(saved);
    }
}
