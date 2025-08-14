package com.example.transactions.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Amount with currency")
public record MoneyDto(
        @Schema(example = "CHF") String currency,
        @Schema(example = "75.00") BigDecimal amount
) {}
