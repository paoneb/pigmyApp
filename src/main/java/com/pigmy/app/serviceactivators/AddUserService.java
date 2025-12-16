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

import java.util.List;

@Component("addUserService")
public class AddUserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AgentRepo agentRepo;

    public ResponseEntity saveUsers(@Body UserData u) throws Exception {
       AgentNew singleAgent = agentRepo.findByAgentCodeAndBankCode(u.getAgentCode(),u.getBankCode()).orElseThrow(() -> new Exception("Agent not found"));

        for (UserList lis : u.getUsers()) {
            userRepo.findByAccountNumberAndAgents_BankCode(lis.getAccountNumber(),u.getBankCode())
                    .map(existing -> {
                        existing.setCurrentBalance(lis.getCurrentBalance());
                        existing.setCustomerName(lis.getCustomerName());
                        existing.setLastDepositDate(lis.getLastDepositDate());
                         userRepo.save(existing);
                        return ResponseEntity.ok("Customer added successfully");
                    })
                    .orElseGet(() -> {
                        // first time save
                        User user = new User();
                        user.setAccountNumber(lis.getAccountNumber());
                        user.setCustomerName(lis.getCustomerName());
                        user.setCurrentBalance(lis.getCurrentBalance());
                        user.setLastDepositDate(lis.getLastDepositDate());
                        user.setAgents(singleAgent);
                         userRepo.save(user);
                        return ResponseEntity.ok("Customer added successfully");
                    });
        }

        return ResponseEntity.ok("Customer added successfully");
    }

    public List<User> fetchCustomers(@Header("agentCode") final Integer agentCode,@Header("bankCode") final String bankCode)
    {
        if (agentCode != null) {
            return userRepo.findByAgents_AgentCodeAndAgents_BankCode(agentCode,bankCode);

        } else {
            return userRepo.findAll();
        }


    }
}
