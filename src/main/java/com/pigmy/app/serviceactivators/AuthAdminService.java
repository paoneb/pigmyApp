package com.pigmy.app.serviceactivators;

import com.pigmy.app.model.AdminLogin;
import com.pigmy.app.repository.AdminLoginRepo;
import com.pigmy.app.repository.UserRepo;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AuthAdminService {

    @Autowired
    private AdminLoginRepo adminLoginRepo;

    public boolean validate(String userName, String password, String bankCode, Exchange e) {
        AdminLogin adminLogin = adminLoginRepo.findByUserNameAndBankCode(userName, bankCode);
        e.setProperty("bankName",adminLogin.getBankName());
        return adminLogin != null && adminLogin.getPassword().equals(password);
    }

}
