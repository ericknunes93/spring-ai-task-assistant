package dio.budgeting.application.dto;

import dio.budgeting.domain.Transaction;
import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
    Long id,
    BigDecimal amount,
    TransactionType type,
    TransactionCategory category,
    String description,
    LocalDateTime createdAt
) {
    public static TransactionResponse fromDomain(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getAmount(),
            transaction.getType(),
            transaction.getCategory(),
            transaction.getDescription(),
            transaction.getCreatedAt()
        );
    }
}
