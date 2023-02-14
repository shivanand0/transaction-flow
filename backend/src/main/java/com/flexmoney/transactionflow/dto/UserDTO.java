package com.flexmoney.transactionflow.dto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @NotNull(message = "Please enter your name")
    @NotBlank(message = "Please enter your name")
    private String userName;
    @NotNull(message = "Please enter your mobile number")
    @NotBlank(message = "Please enter your mobile number")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Please enter a valid mobile number")
    private String mobileNumber;
//    @JsonIgnore
    @NotNull(message = "Please enter the amount")
    @DecimalMin(value="500", message = "Amount must be greater than 500")
    private Double amount;
}
