package com.pigmy.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "account_number")
    private Integer accountNumber;


    @Column(name = "current_balance")
    private long currentBalance;

    @Column(name = "last_deposit_date")
    private String lastDepositDate;

    @Column
    private String schemeId;

    @Column
    private Integer agentCode;

    @Column
    private String bankCode;

    @Column
    private String mobilenumber;

}