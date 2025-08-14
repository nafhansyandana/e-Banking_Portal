package com.example.transactions.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Pagination Result")
public record PageResponse<T>(
        List<T> items,
        @Schema(example = "0") int page,
        @Schema(example = "50") int size,
        @Schema(description = "Is there still a next page", example = "true") boolean hasNext
) {}
