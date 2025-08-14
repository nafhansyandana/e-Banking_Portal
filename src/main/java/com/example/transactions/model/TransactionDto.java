package com.example.transactions.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Account transaction representation")
public record TransactionDto(
        @Schema(example = "89d3o179-abcd-465b-o9ee-e2d5f6ofEld46") String id,
        MoneyDto amount,
        @Schema(example = "CH93-0000-0000-0000-0000-0") String accountIban,
        @Schema(description = "Value date (ISO-8601 yyyy-MM-dd)", example = "2020-10-01") String valueDate,
        @Schema(example = "Online payment CHF") String description
) {}
