package com.pigmy.app.model.sledger;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="sledger_transactions")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SledgerTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long collectedAmount;

    @Column
    private LocalDate collectedDate;

    @Column
    private String schemeId;

    @Column
    private String collectiontype;

    @Column
    private String status;

    @Column
    private long agentDepositId;

    @Column
    private Integer agentCode;

    @Column
    private String bankCode;

    @Column
    private long userId;

    @Column
    private String customerName;

    @Column
    private String accountNumber;

    @Column
    private String transactionId;

    @Column
    private String agentname;

    @Column
    private LocalDate agentDepositedDate;

    @Column
    private String branchCode;

}
