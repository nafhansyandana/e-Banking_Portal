package com.example.transactions.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transaction list + summary response")
public record TransactionsResponse(
        PageResponse<TransactionDto> page,
        TransactionsPageSummaryDto summary
) {}
