package com.flexmoney.transactionflow.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestModel {
    @NotNull(message = "Please enter your name")
    @NotBlank(message = "Please enter your name")
    private String userName;
    @NotNull(message = "Please enter your mobile number")
    @NotBlank(message = "Please enter your mobile number")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Please enter a valid mobile number")
    private String mobileNumber;

    @NotNull(message = "Please enter the amount")
    @DecimalMin(value = "500.00", message = "Amount must be greater than 500")
    private Double amount;
}
