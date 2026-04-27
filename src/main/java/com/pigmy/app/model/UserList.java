package com.pigmy.app.model;

import lombok.Data;

@Data
public class UserList {


    private String customerName;


    private Integer accountNumber;


    private long currentBalance;


    private String lastDepositDate;

    private String schemeId;

}
