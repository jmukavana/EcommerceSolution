package com.jamlsolution.backend.order.returns.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateReturnRequest(
        @NotBlank String reason,
        @NotEmpty @Valid List<ReturnItemRequest> items
) {}
