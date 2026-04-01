package com.pigmy.app.model;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;

@Data
public class UserList {


    private String customerName;


    private Integer accountNumber;


    private long currentBalance;


    private String lastDepositDate;

    private String schemeId;

}
