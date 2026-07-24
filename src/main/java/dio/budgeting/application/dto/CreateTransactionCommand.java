package dio.budgeting.application.dto;

import dio.budgeting.domain.TransactionCategory;
import dio.budgeting.domain.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransactionCommand(
    @NotNull(message = "O valor da transação é obrigatório.")
    @Positive(message = "O valor da transação deve ser maior que zero.")
    BigDecimal amount,

    @NotNull(message = "O tipo da transação (RECEITA ou DESPESA) é obrigatório.")
    TransactionType type,

    TransactionCategory category,

    String description
) {}
