package com.privat.paymentsrest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChargeDto(@NotNull(message = "chargeId is mandatory")
                        UUID chargeId,
                        UUID payment,
                        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                        LocalDateTime chargeTime,
                        @NotNull(message = "amount is mandatory")
                        BigDecimal amount,
                        Status status) {
}
