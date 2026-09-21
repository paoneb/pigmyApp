package com.pigmy.app.model.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FetchTransactionResponse {

    private Long trasactionId;
    private Integer accountNumber;
    private String customerName;
    private long collectedAmount;
    private String schemeName;
    private String status;


}
