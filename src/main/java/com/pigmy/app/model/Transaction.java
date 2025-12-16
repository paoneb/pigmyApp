package com.pigmy.app.model;


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

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(name = "date")
    private LocalDate depositeDate;

    // Many transactions belong to one agent
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="agent_code", referencedColumnName="agent_code"),
            @JoinColumn(name="bank_code", referencedColumnName="bank_code")
    })
    @JsonIgnore
    private AgentNew agents;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonIgnore
    private User user;


}
