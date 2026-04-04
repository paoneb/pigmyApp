package com.pigmy.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name="transactions")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double collectedAmount;

    @Column
    private LocalDate collectedDate;

    @Column
    private String schemename;

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
    private Integer accountNumber;

    @Column
    private String schemeId;

}
