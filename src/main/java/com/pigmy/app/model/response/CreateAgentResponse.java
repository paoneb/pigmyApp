package com.pigmy.app.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CreateAgentResponse {

    private String status;
    private String message;
    private Integer agentCode;
    private String bankCode;
    private String name;


}
