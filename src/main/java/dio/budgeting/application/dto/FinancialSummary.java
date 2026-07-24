package dio.budgeting.application.dto;

import java.math.BigDecimal;

public record FinancialSummary(
    BigDecimal totalReceitas,
    BigDecimal totalDespesas,
    BigDecimal saldoAtual
) {}
