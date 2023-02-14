package com.flexmoney.transactionflow.model;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="user_table")
public class UserModel {
    @NotNull(message = "Please enter your name")
    @NotBlank(message = "Please enter your name")
    private String userName;
    @NotNull(message = "Please enter your mobile number")
    @NotBlank(message = "Please enter your mobile number")
    @Pattern(regexp="(^$|[0-9]{10})",message = "Please enter a valid mobile number")
    private String mobileNumber;
    private double creditLimit;
    private Long lastFourDigitsOfPan;
    @ElementCollection
    private List<Integer> lenderId;
    @CreationTimestamp
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}