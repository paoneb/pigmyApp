package com.pigmy.app.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String bankName;
    private String bankCode;
    private String token;
    private String city;
    private List<SubBranchDTO> subBranches;
    private String bankType;
}

