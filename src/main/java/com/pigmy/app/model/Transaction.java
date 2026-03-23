package com.pigmy.app.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name="transaction")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double collectedAmount;

    @Column(name = "collected_date")
    private LocalDate depositeDate;

    @Column
    private String schemename;

    @Column
    private String ledgergroup;

    @Column
    private String collectiontype;

    @Column
    private String status;

    // Many transactions belong to one agent
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="agent_code", referencedColumnName="agent_code"),
            @JoinColumn(name="bank_code", referencedColumnName="bank_code")
    })
    @JsonIgnore
    private AgentNew agents;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JoinColumn(name = "customer_name", referencedColumnName = "customer_name")
    @JsonIgnore
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "agent_deposit_id", referencedColumnName = "id")
    private AgentDeposit agentDeposit;


}
