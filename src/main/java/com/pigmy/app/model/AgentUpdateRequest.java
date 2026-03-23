package com.pigmy.app.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AgentUpdateRequest {
    private Long id;
    private String name;
    private String address;
    private String password;
    private String phone;
    private String email;
    private Integer agentCode;
    private String bankCode;
    private String type;
    private long limitAmount;
    private String status;
}