package com.pigmy.app.model.peocit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pigmy.app.model.response.UserCollection;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PeocitAgentDepositResponse {

    private Integer agentCode;
    private String bankCode;
    private long totalDepositedAmount;
    private String depositedDate;
    private String vpncode;
    private List<PeocitUserCollection> users;

}
