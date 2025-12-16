package com.pigmy.app.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

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

    @Column(name = "account_number",unique = true)
    private Integer accountNumber;


    @Column(name = "current_balance")
    private long currentBalance;

    @Column(name = "last_deposit_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastDepositDate;

    @ManyToOne
    @JoinColumn(name = "agent_code", referencedColumnName = "agent_code")
    @JsonIgnore
    private AgentNew agents;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Transaction> transactions;

    // getters and setters
}