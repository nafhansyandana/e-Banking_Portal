package com.example.transactions.config;

import com.example.transactions.stream.TransactionEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionEvent> kafkaListenerContainerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrap,
            @Value("${app.kafka.consumer-group}") String groupId
    ) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        var valueDeserializer = new JsonDeserializer<>(TransactionEvent.class);
        valueDeserializer.addTrustedPackages("*");

        var consumerFactory = new DefaultKafkaConsumerFactory<>(
                props, new StringDeserializer(), valueDeserializer
        );

        var factory = new ConcurrentKafkaListenerContainerFactory<String, TransactionEvent>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
