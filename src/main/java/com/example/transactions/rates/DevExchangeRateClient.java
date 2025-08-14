package com.example.transactions.rates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class DevExchangeRateClient implements ExchangeRateClient {
    private final Map<String, BigDecimal> eurBased;

    public DevExchangeRateClient(
            @Value("${app.rates.EUR:1.0}") BigDecimal eur,
            @Value("${app.rates.CHF:1.05}") BigDecimal chf,
            @Value("${app.rates.GBP:1.16}") BigDecimal gbp
    ) {
        this.eurBased = Map.of(
                "EUR", eur,
                "CHF", chf,
                "GBP", gbp
        );
    }

    @Override
    public BigDecimal rate(String fromCurrency, String toCurrency) {
        String from = fromCurrency == null ? "EUR" : fromCurrency.toUpperCase();
        String to = toCurrency == null ? "EUR" : toCurrency.toUpperCase();

        if (from.equals(to)) return BigDecimal.ONE;

        // Conversion via EUR as the pivot: from -> EUR, then EUR -> to
        BigDecimal fromToEur = eurBased.getOrDefault(from, BigDecimal.ONE);
        BigDecimal eurToTo = BigDecimal.ONE.divide(eurBased.getOrDefault(to, BigDecimal.ONE), 8, java.math.RoundingMode.HALF_UP);
        return fromToEur.multiply(eurToTo);
    }
}
