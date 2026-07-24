package dio.budgeting.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Transaction {
    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private TransactionCategory category;
    private String description;
    private LocalDateTime createdAt;

    public Transaction(Long id, BigDecimal amount, TransactionType type, TransactionCategory category, String description) {
        Assert.notNull(amount, "Amount must not be null");
        Assert.isTrue(amount.compareTo(BigDecimal.ZERO) > 0, "Amount must be positive");
        Assert.notNull(type, "Type must not be null");

        this.id = id;
        this.amount = amount;
        this.type = type;
        this.category = category != null ? category : TransactionCategory.OUTROS;
        this.description = description != null ? description : "";
        this.createdAt = LocalDateTime.now();
    }
}
