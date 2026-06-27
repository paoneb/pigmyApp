package com.pigmy.app.model.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class SearchTransactionResponse {

    private Integer accountNumber;
    private String customerName;
    private Double collectedAmount;
    private String schemeName;
    private String status;
    private  String agentName;
    private String collectedDate;

}
