package com.pigmy.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDate;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AgentDepositRequest {


    private String name;
    private int agentCode;
    private String bankCode;
    private double depositingAmount;
    private String voucherId;
    private String dateOfCollectedAmount;
    private LocalDate depositDate;

}
