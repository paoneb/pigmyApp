package com.pigmy.app.model.sledger;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchSledgerTransactionResponse {

    private String accountNumber;
    private String customerName;
    private long collectedAmount;
    private String schemeName;
    private String status;
    private  String agentName;
    private String collectedDate;

}
