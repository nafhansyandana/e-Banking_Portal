package com.example.transactions.rates;

import com.example.transactions.model.MoneyDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateService {
    private final ExchangeRateClient client;

    public ExchangeRateService(ExchangeRateClient client) {
        this.client = client;
    }

    public MoneyDto convert(MoneyDto money, String targetCcy) {
        if (money.currency().equalsIgnoreCase(targetCcy)) return money;
        BigDecimal r = client.rate(money.currency(), targetCcy);
        BigDecimal converted = money.amount().multiply(r).setScale(2, RoundingMode.HALF_UP);
        return new MoneyDto(targetCcy.toUpperCase(), converted);
    }
}
