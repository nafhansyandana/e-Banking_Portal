package com.example.transactions.config;

import com.example.transactions.store.TransactionStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreConfig {
    @Bean
    public TransactionStore transactionStore() {
        return new TransactionStore();
    }
}
