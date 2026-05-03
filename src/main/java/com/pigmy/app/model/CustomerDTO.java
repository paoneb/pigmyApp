package com.pigmy.app.model;


import lombok.Data;

@Data
public class CustomerDTO {
    private Integer accountNumber;
    private String bankCode;
    private String mobilenumber;

    public CustomerDTO(Integer accountNumber, String bankCode, String mobilenumber) {
        this.accountNumber = accountNumber;
        this.bankCode = bankCode;
        this.mobilenumber = mobilenumber;
    }

    // getters
}