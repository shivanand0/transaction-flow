package com.flexmoney.transactionflow.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class EUserVerificationInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonProperty("txnId")
    private UUID detailsId;
    @NotNull(message = "Please send the userId")
    private Long userId;

    private UUID trackId;

    @NotNull(message = "Please enter your mobile number")
    @NotBlank(message = "Please enter your mobile number")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Please enter a valid mobile number")
    private String mobileNumber;

    @NotNull(message = "Please send the amount")
    private double amount;

    public Integer txnCount;

    public Integer PanOTPCount;

    public Integer MobOTPCount;

    private String status;

    private String remark;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

}
