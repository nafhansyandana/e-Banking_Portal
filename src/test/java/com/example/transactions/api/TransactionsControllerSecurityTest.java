package com.example.transactions.api;

import com.example.transactions.model.MoneyDto;
import com.example.transactions.rates.ExchangeRateService;
import com.example.transactions.store.TransactionStore;
import com.example.transactions.stream.TransactionEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransactionsController.class)
class TransactionsControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TransactionStore store;

    @MockBean
    ExchangeRateService rates;

    @Test
    void getTransactions_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/transactions").param("month", "2020-10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "P-TEST-USER")
    void getTransactions_withMockUser_returns200() throws Exception {
        // new TransactionEvent(id, accountIban, currency, amount, valueDate, description, customerId)
        var ev = new TransactionEvent(
                "id-1",
                "CH93-0000-0000-0000-0000-0",
                "EUR",
                new BigDecimal("5.00"),
                LocalDate.parse("2020-10-01"),
                "desc",
                "P-TEST-USER"
        );

        Mockito.when(store.page(
                        anyString(),
                        any(YearMonth.class),
                        nullable(String.class),
                        anyInt(),
                        anyInt()))
                .thenReturn(List.of(ev));

        Mockito.when(store.hasNext(
                        anyString(),
                        any(YearMonth.class),
                        nullable(String.class),
                        anyInt(),
                        anyInt()))
                .thenReturn(false);

        Mockito.when(rates.convert(any(MoneyDto.class), anyString()))
                .thenAnswer(inv -> {
                    MoneyDto m = inv.getArgument(0);
                    String to = ((String) inv.getArgument(1)).toUpperCase();
                    return new MoneyDto(to, m.amount());
                });

        mockMvc.perform(get("/transactions")
                        .param("month", "2020-10")
                        .param("page", "0")
                        .param("size", "50")
                        .param("baseCurrency", "EUR"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }
}
