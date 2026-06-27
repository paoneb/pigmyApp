package com.pigmy.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubBranchDTO {
    private String bankCode;
    private String bankName;
    private String city;

    public SubBranchDTO(String bankCode, String bankName, String city) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.city = city;
    }
}
