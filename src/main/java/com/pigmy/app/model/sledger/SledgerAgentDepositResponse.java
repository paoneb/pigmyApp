package com.pigmy.app.model.sledger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pigmy.app.model.peocit.PeocitUserCollection;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SledgerAgentDepositResponse {

    private Integer agentCode;
    private String bankCode;
    private long totalDepositedAmount;
    private String depositedDate;
    private List<SledgerUserCollection> users;

}
