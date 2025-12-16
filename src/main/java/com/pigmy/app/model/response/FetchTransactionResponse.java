package com.pigmy.app.model.response;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class FetchTransactionResponse {

    private Long trasactionId;
    private Integer accountNumber;
    private String customerName;
    private BigDecimal depositAmount;


}
