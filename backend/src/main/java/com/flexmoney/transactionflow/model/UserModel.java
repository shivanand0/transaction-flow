package com.flexmoney.transactionflow.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
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