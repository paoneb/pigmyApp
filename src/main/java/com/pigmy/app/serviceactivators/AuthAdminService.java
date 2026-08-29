package com.pigmy.app.serviceactivators;

import com.pigmy.app.model.AdminLogin;
import com.pigmy.app.model.LoginResponse;
import com.pigmy.app.model.SubBranchDTO;
import com.pigmy.app.repository.AdminLoginRepo;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;


@Component("authAdminService")
public class AuthAdminService {

    @Autowired
    private AdminLoginRepo adminLoginRepo;

    public boolean validate(String userName, String password, String bankCode, Exchange e) {
        AdminLogin adminLogin = adminLoginRepo.findByUserNameAndBankCode(userName, bankCode);
        e.setProperty("bankName",adminLogin.getBankName());
        e.setProperty("city",adminLogin.getCity());
        e.setProperty("bankType",adminLogin.getBankType());
        long daysElapsed = ChronoUnit.DAYS.between(adminLogin.getPurchaseDate(), LocalDate.now());
        int standardLimit = 365;
        int totalAllowedDays = standardLimit + adminLogin.getGraceDays();
        if (daysElapsed > totalAllowedDays) {
            throw new RuntimeException("Subscription and grace period completely exhausted. Please contact the bank for renewal.");
        }
        if(adminLogin.isMainBranch())
        {
            List<SubBranchDTO> subBranches= adminLoginRepo.findByParentId(adminLogin.getBankCode());
            e.setProperty("subBranches",subBranches);
        }
        return adminLogin.getPassword().equals(password);
    }


    public void fetchDashboardData(String bankCode, Exchange e) {
        List<Object[]> dashboardData = adminLoginRepo.findByBankCode(bankCode);
        LocalDate date=  LocalDate.parse(dashboardData.get(0)[0].toString());
       int count= Integer.parseInt(dashboardData.get(0)[1].toString());
        LocalDate expiryDate = date.plusYears(1);
        long daysLeft = Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), expiryDate));

        e.getIn().setBody(Map.of(
                "purchaseDate", date.toString(),
                "NoOfLicencedPurchased", count,
             "daysLeft", daysLeft,
                "expiryDate", expiryDate.toString()
        ));
    }

}
