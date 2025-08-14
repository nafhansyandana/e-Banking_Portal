package com.example.transactions.api;

import com.example.transactions.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionsController {

    @GetMapping
    @Operation(
        summary = "List of transactions for a specific month (paginated)",
        description = """
            Returns a list of transactions belonging to the logged-in user for a specific month.
            It will later read from Kafka and calculate the total debit/credit in baseCurrency.
            Currently still a stub.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content)
        }
    )
    public ResponseEntity<TransactionsResponse> listTransactions(
            @Parameter(description = "Month in the format yyyy-MM, e.g., 2020-10", example = "2020-10")
            @RequestParam String month,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "50")
            @RequestParam(defaultValue = "50") int size,
            @Parameter(description = "Target currency for conversion", example = "EUR")
            @RequestParam(defaultValue = "EUR") String baseCurrency,
            @Parameter(description = "Filter by IBAN (optional)", example = "CH93-0000-0000-0000-0000-0")
            @RequestParam(required = false) String accountIban,
            @AuthenticationPrincipal Jwt jwt
    ) {
        // ==== DUMMY DATA (temp) ====
        String userId = (jwt != null) ? jwt.getSubject() : "anonymous";
        System.out.println("DEBUG userId=" + userId);
        var t1 = new TransactionDto(
                "89d3o179-abcd-465b-o9ee-e2d5f6ofEld46",
                new MoneyDto("CHF", new BigDecimal("75.00")),
                accountIban != null ? accountIban : "CH93-0000-0000-0000-0000-0",
                month + "-01",
                "Online payment CHF"
        );
        var t2 = new TransactionDto(
                "11111111-2222-3333-4444-555555555555",
                new MoneyDto("EUR", new BigDecimal("-20.00")),
                accountIban != null ? accountIban : "CH93-0000-0000-0000-0000-0",
                month + "-02",
                "Card fee"
        );

        var pageResp = new PageResponse<>(
                List.of(t1, t2),
                page,
                size,
                false
        );

        // Summary dummy
        var summary = new TransactionsPageSummaryDto(
                baseCurrency,
                new MoneyDto(baseCurrency, new BigDecimal("20.00")), // debit total
                new MoneyDto(baseCurrency, new BigDecimal("75.00"))  // credit total
        );

        return ResponseEntity.ok(new TransactionsResponse(pageResp, summary));
    }
}
