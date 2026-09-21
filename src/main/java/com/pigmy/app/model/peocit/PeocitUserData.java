package com.pigmy.app.model.peocit;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.pigmy.app.model.UserList;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PeocitUserData {

    private Integer agentCode;

    private String bankCode;

    private String vpncode;

    private List<PeocitUserList> users;
}
