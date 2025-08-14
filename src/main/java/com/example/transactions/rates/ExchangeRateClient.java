package com.example.transactions.rates;

import java.math.BigDecimal;

public interface ExchangeRateClient {
    /**
        Get the "from -> to" exchange rate (current rate)
        Example: rate(EUR, EUR) = 1.0; rate(CHF, EUR) ~ 1.05
     */
    BigDecimal rate(String fromCurrency, String toCurrency);
}
