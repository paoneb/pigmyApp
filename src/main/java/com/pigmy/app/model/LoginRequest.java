package com.pigmy.app.model;


import lombok.Data;

@Data
public class LoginRequest {
    private String bankCode;
    private String userName;
    private String password;
}