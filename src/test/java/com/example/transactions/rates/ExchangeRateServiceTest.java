package com.example.transactions.rates;

import com.example.transactions.model.MoneyDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateServiceTest {
    @Test
    void convert_eurToChf_usesProvidedRate() {
        // Stub client: rate(EUR->CHF) = 0.95238095
        ExchangeRateClient client = (from, to) -> {
            if ("EUR".equalsIgnoreCase(from) && "CHF".equalsIgnoreCase(to)) {
                return new BigDecimal("0.95238095");
            }
            return BigDecimal.ONE;
        };

        var svc = new ExchangeRateService(client);

        var res = svc.convert(new MoneyDto("EUR", new BigDecimal("20.00")), "CHF");
        assertEquals("CHF", res.currency());
        assertEquals(new BigDecimal("19.05"), res.amount()); // 20 * 0.95238 = 19.05
    }

    @Test
    void convert_sameCurrency_returnsOriginal() {
        ExchangeRateClient client = (f, t) -> BigDecimal.ONE;
        var svc = new ExchangeRateService(client);

        var money = new MoneyDto("EUR", new BigDecimal("10.00"));
        var res = svc.convert(money, "EUR");

        assertEquals("EUR", res.currency());
        assertEquals(new BigDecimal("10.00"), res.amount());
    }
}
