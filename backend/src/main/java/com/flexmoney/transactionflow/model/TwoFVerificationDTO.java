package com.flexmoney.transactionflow.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwoFVerificationDTO {
    @NotNull(message = "Provide trackId")
    @NotBlank(message = "Provide trackId")
    private UUID trackId;

    @NotNull(message = "Provide OTP")
    @NotBlank(message = "Provide OTP")
    private long receivedOtp;
}
