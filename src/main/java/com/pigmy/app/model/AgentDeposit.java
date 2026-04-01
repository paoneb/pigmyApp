package com.pigmy.app.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="agents_deposit")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AgentDeposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "depositing_amount")
    private double depositingAmount;

    @Column(name = "voucher_id")
    private String voucherId;

    @Column
    private String DateOfCollectedAmount;

    @Column(name = "deposit_date")
    private LocalDate depositDate;

    @Column
    private Integer agentCode;

    @Column
    private String bankCode;

}
