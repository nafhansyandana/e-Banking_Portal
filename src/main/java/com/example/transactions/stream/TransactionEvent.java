package com.example.transactions.stream;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionEvent(
        String id,
        String accountIban,
        String currency,
        BigDecimal amount,
        LocalDate valueDate,
        String description,
        String customerId
) {}
