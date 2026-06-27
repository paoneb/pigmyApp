package com.pigmy.app.serviceactivators;

import com.pigmy.app.model.AdminLogin;
import com.pigmy.app.model.LoginResponse;
import com.pigmy.app.model.SubBranchDTO;
import com.pigmy.app.repository.AdminLoginRepo;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class AuthAdminService {

    @Autowired
    private AdminLoginRepo adminLoginRepo;

    public boolean validate(String userName, String password, String bankCode, Exchange e) {
        AdminLogin adminLogin = adminLoginRepo.findByUserNameAndBankCode(userName, bankCode);
        e.setProperty("bankName",adminLogin.getBankName());
        e.setProperty("city",adminLogin.getCity());
        if(adminLogin.isMainBranch()==true)
        {
            List<SubBranchDTO> subBranches= adminLoginRepo.findByParentId(adminLogin.getBankCode());
            e.setProperty("subBranches",subBranches);
        }
        return adminLogin != null && adminLogin.getPassword().equals(password);
    }

}
