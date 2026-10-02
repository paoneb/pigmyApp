package com.pigmy.app.model.sledger;

import lombok.Data;

@Data
public class  SledgerUserList {


    private String customerName;


    private String accountNumber;


    private long currentBalance;


    private String lastDepositDate;

    private String schemeId;

}
