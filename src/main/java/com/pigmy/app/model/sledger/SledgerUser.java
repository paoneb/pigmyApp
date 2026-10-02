package com.pigmy.app.model.sledger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "sledger_user")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SledgerUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sledger_user_seq")
    @SequenceGenerator(
            name = "sledger_user_seq",
            sequenceName = "sledger_user_seq",
            allocationSize = 100 // must match batch_size
    )
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "account_number")
    private String accountNumber;


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

    @Column
    private String branchCode;
}
