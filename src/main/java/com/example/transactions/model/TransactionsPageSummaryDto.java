package com.example.transactions.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Summary per page (total debit/credit in baseCurrency)")
public record TransactionsPageSummaryDto(
        @Schema(example = "EUR") String baseCurrency,
        @Schema(description = "Total debit on the current page is converted to baseCurrency")
        MoneyDto pageDebitTotal,
        @Schema(description = "Total credit on the current page is converted to baseCurrency")
        MoneyDto pageCreditTotal
) {}
