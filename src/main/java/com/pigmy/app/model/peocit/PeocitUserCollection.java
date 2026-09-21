package com.pigmy.app.model.peocit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PeocitUserCollection {

    private String schemeAccntNumber;
    private long collectedAmount;
    private long finalAmount;
    private String customerName;
    private String collectedDate;

}
