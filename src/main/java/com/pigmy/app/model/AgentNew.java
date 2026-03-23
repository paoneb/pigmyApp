package com.pigmy.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="agents")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AgentNew {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="agent_name")
    private String name;

    @Column(name="agent_address")
    private String address;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String phone;
    private String email;


    @Column(name = "agent_code")
    private Integer agentCode;

    @Column(name = "bank_code")
    private String bankCode;
    private String type;
    private long limitAmount;
    private String status;


    @OneToMany(mappedBy = "agents")
    @JsonIgnore
    private List<Transaction> transactions ;

    @OneToMany(mappedBy = "agents")
    @JsonIgnore
    private List<User> user ;

    @OneToMany(mappedBy = "agents")
    @JsonIgnore
    private List<AgentDeposit> agentDeposits;

}
