package com.familyhub.backend.dto;

import com.familyhub.backend.enums.BillType;
import com.familyhub.backend.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public final class BillDtos {
    private BillDtos() {
    }

    public record UpsertBillRequest(
            @NotNull(message = "账单类型不能为空") BillType type,
            @NotNull(message = "金额不能为空") @DecimalMin(value = "0.01", message = "金额必须大于 0") BigDecimal amount,
            @NotBlank(message = "分类不能为空") String category,
            @NotNull(message = "支付方式不能为空") PaymentMethod paymentMethod,
            @NotNull(message = "账单日期不能为空") LocalDate billDate,
            @NotNull(message = "成员不能为空") Long memberId,
            String note
    ) {
    }

    public record BillResponse(
            Long id,
            Long familyId,
            Long memberId,
            BillType type,
            BigDecimal amount,
            String category,
            PaymentMethod paymentMethod,
            LocalDate billDate,
            String note
    ) {
    }

    public record BillStatisticsResponse(
            BigDecimal monthIncome,
            BigDecimal monthExpense,
            BigDecimal balance,
            Map<String, BigDecimal> expenseByCategory
    ) {
    }
}
