package com.pigmy.app.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AgentDepositResponse {

    private Integer agentCode;
    private String bankCode;
    private BigDecimal totalCollectedAmount;
    private String depositedDate;
    private List<UserCollection> users;

}
