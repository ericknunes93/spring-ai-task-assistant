package dio.budgeting.infrastructure.http;

import dio.budgeting.application.CreateTransactionUseCase;
import dio.budgeting.application.GetFinancialSummaryUseCase;
import dio.budgeting.application.ListTransactionsUseCase;
import dio.budgeting.application.dto.CreateTransactionCommand;
import dio.budgeting.application.dto.FinancialSummary;
import dio.budgeting.application.dto.TransactionResponse;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionUseCase createTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetFinancialSummaryUseCase getFinancialSummaryUseCase;

    public TransactionController(CreateTransactionUseCase createTransactionUseCase,
                                 ListTransactionsUseCase listTransactionsUseCase,
                                 GetFinancialSummaryUseCase getFinancialSummaryUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getFinancialSummaryUseCase = getFinancialSummaryUseCase;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody @Valid CreateTransactionCommand command) {
        TransactionResponse response = createTransactionUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionCategory category) {
        List<TransactionResponse> transactions = listTransactionsUseCase.execute(type, category);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/summary")
    public ResponseEntity<FinancialSummary> getSummary(@RequestParam(required = false) TransactionCategory category) {
        FinancialSummary summary = getFinancialSummaryUseCase.execute(category);
        return ResponseEntity.ok(summary);
    }
}
