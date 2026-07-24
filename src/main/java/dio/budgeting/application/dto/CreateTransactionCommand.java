package dio.budgeting.application.dto;

import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionType;

import java.math.BigDecimal;

public record CreateTransactionCommand(
    BigDecimal amount,
    TransactionType type,
    TransactionCategory category,
    String description
) {}
