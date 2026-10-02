package com.pigmy.app.model.sledger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SledgerUserCollection {

    private String schemeId;
    private long collectedAmount;
    private String  accountNumber;
    private String customerName;
    private String collectedDate;
    private String branchCode;

}
