package com.flexmoney.transactionflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseModel {
    @JsonProperty("txnId")
    private UUID detailsId;
    private UUID trackId;

    @NotNull(message = "Please send the userId")
    private Long userId;

    @NotNull(message = "Please enter your mobile number")
    @NotBlank(message = "Please enter your mobile number")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Please enter a valid mobile number")
    private String mobileNumber;

    @NotNull(message = "Please send the amount")
    private double amount;

    private Integer statusCode;
}
