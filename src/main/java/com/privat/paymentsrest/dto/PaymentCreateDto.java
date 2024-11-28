package com.privat.paymentsrest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreateDto(@NotBlank(message = "Card id is mandatory")
                               UUID card,
                               @NotBlank(message = "iban is mandatory")
                               @Size(min = 15, max = 34, message = "input size should be more than 14 and less than 34 ")
                               String iban,
                               @Size(min = 5, max = 9, message = "input size should be more than 4 and less than 10 ")
                               String mfo,
                               @Size(min = 5, max = 12, message = "input size should be more than4 and less than 13 ")
                               String zkpo,
                               @NotBlank(message = "Receiver name is mandatory")
                               String receiverName,
                               @NotNull(message = "Amount is mandatory")
                               BigDecimal amount,
                               @NotNull(message = "Withdrawal period is mandatory")
                               Long withdrawalPeriod) {
}
