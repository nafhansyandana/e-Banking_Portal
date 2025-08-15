package com.example.transactions.stream;

import com.example.transactions.store.TransactionStore;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionsConsumer {
    private final TransactionStore store;

    public TransactionsConsumer(TransactionStore store) {
        this.store = store;
    }

    @KafkaListener(
            topics = "${app.kafka.topic}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onMessage(TransactionEvent event) {
        store.add(event);
        System.out.println("DEBUG consumed event id=" + event.id() +
                " user=" + event.customerId() +
                " iban=" + event.accountIban() +
                " date=" + event.valueDate() +
                " amt=" + event.amount() + " " + event.currency());
    }
}
