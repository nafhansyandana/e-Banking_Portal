package com.example.transactions.stream;

import com.example.transactions.model.MoneyDto;
import com.example.transactions.model.TransactionDto;

public class TransactionMapper {
    public static TransactionDto toDto(TransactionEvent e) {
        return new TransactionDto(
                e.id(),
                new MoneyDto(e.currency(), e.amount()),
                e.accountIban(),
                e.valueDate().toString(),
                e.description()
        );
    }
}
