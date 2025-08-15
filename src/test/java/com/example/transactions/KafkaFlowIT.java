package com.example.transactions;

import com.example.transactions.stream.TransactionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.redpanda.RedpandaContainer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class KafkaFlowIT {
    // Redpanda
    @Container
    static RedpandaContainer kafka = new RedpandaContainer("docker.redpanda.com/redpandadata/redpanda:v23.3.6");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.kafka.listener.missing-topics-fatal", () -> "false");
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    KafkaTemplate<String, TransactionEvent> template;

    @BeforeEach
    void setUpProducer() {
        var producerFactory = new DefaultKafkaProducerFactory<String, TransactionEvent>(
                Map.of(
                        "bootstrap.servers", kafka.getBootstrapServers(),
                        "key.serializer", StringSerializer.class,
                        "value.serializer", JsonSerializer.class
                )
        );
        template = new KafkaTemplate<>(producerFactory);
    }

    @Test
    void endToEnd_produceConsumeExposeViaApi() throws Exception {
        String userId = "P-0123456789";
        var event = new TransactionEvent(
                "ev-1",
                "CH93-0000-0000-0000-0000-0",
                "EUR",
                new BigDecimal("12.34"),
                LocalDate.parse("2020-10-03"),
                "Test txn",
                userId
        );

        String topic = "transactions";
        template.send(topic, event.id(), event).get();

        String url = "http://localhost:" + port +
                "/transactions?month=2020-10&page=0&size=50&baseCurrency=EUR";

        var headers = new org.springframework.http.HttpHeaders();
        headers.add("Authorization",
                "Bearer " +
                        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." +
                        "eyJzdWIiOiJQLTAxMjM0NTY3ODkiLCJpc3MiOiJkZW1vLWViYW5raW5nIiwiaWF0IjoxNzU1MTY3OTQ5LCJleHAiOjE3NTUyNTQzNDksInNjb3BlIjoidHJhbnNhY3Rpb25zOnJlYWQifQ." +
                        "Y6V4648tVh4bPkqYp9GQeWeEUYjfmznRNQgTxrL9qIY");

        var entity = new org.springframework.http.HttpEntity<>(headers);

        long deadline = System.currentTimeMillis() + 10_000;
        String body = null;
        int status = 0;

        while (System.currentTimeMillis() < deadline) {
            var resp = rest.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);
            status = resp.getStatusCode().value();
            body = resp.getBody();
            if (status == 200 && body != null && body.contains("\"id\":\"ev-1\"")) {
                break;
            }
            Thread.sleep(200);
        }

        Assertions.assertEquals(200, status, "HTTP status must be 200");
        Assertions.assertNotNull(body, "The body must not be null");
        Assertions.assertTrue(body.contains("\"id\":\"ev-1\""), "The response must include the event we send");
    }
}
