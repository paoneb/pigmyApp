package com.pigmy.app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FetchPastDepositsResponse {

    private long depositId;
    private Integer agentCode;
    private String bankCode;
    private String depositDate;
    private double totalDepositedAmount;

}
