package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.AgentNew;
import com.pigmy.app.model.User;
import com.pigmy.app.model.UserData;
import com.pigmy.app.model.UserList;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.repository.UserRepo;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component("addUserService")
public class AddUserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AgentRepo agentRepo;

    public ResponseEntity saveUsers(@Body UserData u) throws Exception {
       AgentNew singleAgent = agentRepo.findByAgentCodeAndBankCode(u.getAgentCode(),u.getBankCode())
               .orElseThrow(() -> new Exception("Agent not found"));

        List<User> toSave = new ArrayList<>();

        for (UserList lis : u.getUsers()) {
            userRepo.findByAccountNumberAndBankCode(lis.getAccountNumber(),u.getBankCode())
                    .ifPresentOrElse(existing -> {
                        existing.setSchemeId(lis.getSchemeId());
                        existing.setCurrentBalance(lis.getCurrentBalance());
                        existing.setCustomerName(lis.getCustomerName());
                        existing.setLastDepositDate(lis.getLastDepositDate());
                        toSave.add(existing);
                    },
                            () -> {
                        // first time save
                        User user = new User();
                        user.setSchemeId(lis.getSchemeId());
                        user.setAccountNumber(lis.getAccountNumber());
                        user.setCustomerName(lis.getCustomerName());
                        user.setCurrentBalance(lis.getCurrentBalance());
                        user.setLastDepositDate(lis.getLastDepositDate());
                        user.setBankCode(u.getBankCode());
                        user.setAgentCode(u.getAgentCode());
                        toSave.add(user);
                    });
        }
        userRepo.saveAll(toSave);

        return ResponseEntity.ok("Customer added successfully");
    }

    public List<User> fetchCustomers(@Header("agentCode") final Integer agentCode,@Header("bankCode") final String bankCode)
    {
        if (agentCode != null) {
            return userRepo.findUsersByAgentCode_bankCode(agentCode,bankCode);

        } else {
            return userRepo.findAll();
        }


    }
}
