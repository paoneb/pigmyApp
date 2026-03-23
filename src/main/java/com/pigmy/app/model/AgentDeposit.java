package com.pigmy.app.model;


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

    private String DateOfCollectedAmount;

    @Column(name = "deposit_date")
    private LocalDate depositDate;


   // @OneToMany(mappedBy = "agentDeposit", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonIgnore
    //private List<Transaction> transactions;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name="agent_code", referencedColumnName="agent_code"),
            @JoinColumn(name="bank_code", referencedColumnName="bank_code"),
            @JoinColumn(name="agent_name", referencedColumnName="agent_name")
    })
    @JsonIgnore
    private AgentNew agents;

}
