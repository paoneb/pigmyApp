package com.pigmy.app.model.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
public class FetchTransactionResponse {

    private Long trasactionId;
    private Integer accountNumber;
    private String customerName;
    private Double depositAmount;


}
