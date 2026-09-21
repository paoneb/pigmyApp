package com.pigmy.app.model.peocit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Table(name="peocit_transactions")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PeocitTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private long collectedAmount;

    @Column
    private long finalAmount;

    @Column
    private LocalDate collectedDate;

    @Column
    private String schemename;

    @Column
    private String schemeId;

    @Column
    private String collectiontype;

    @Column
    private LocalDate agentDepositedDate;

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
}
