package com.pigmy.app.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String bankName;
    private String bankCode;
    private String token;

}
