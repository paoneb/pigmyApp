package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.*;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.repository.UserDetailsRepo;
import com.pigmy.app.repository.UserRepo;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("addUserService")
public class AddUserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private UserDetailsRepo userDetailsRepo;

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

    public ResponseEntity addMobileNumberService(@Body UploadMobileNumberRequest uploadMobileNumberRequest)
    {
        List<UploadMobileNumber> saveMobileNumber = new ArrayList<>();

        for (UserDetails details : uploadMobileNumberRequest.getUserDetailsList())
        {
            userDetailsRepo.findByAccountNumberAndBankCode(details.getAccountNumber(),uploadMobileNumberRequest.getBankCode())
                    .ifPresentOrElse(existing -> {
                       existing.setAccountNumber(details.getAccountNumber());
                       existing.setMobilenumber(details.getMobilenumber());
                        saveMobileNumber.add(existing);
                    },
                            ()->{
                                UploadMobileNumber uploadMobileNumber=new UploadMobileNumber();
                                uploadMobileNumber.setAccountNumber(details.getAccountNumber());
                                uploadMobileNumber.setMobilenumber(details.getMobilenumber());
                                uploadMobileNumber.setBankCode(uploadMobileNumberRequest.getBankCode());
                                saveMobileNumber.add(uploadMobileNumber);

                            });


        }
        List<UploadMobileNumber> mn= userDetailsRepo.saveAll(saveMobileNumber);

        if(!mn.isEmpty())
        {
            Integer listOfUsers= userRepo.updateMobileNumbers(uploadMobileNumberRequest.getBankCode());

            System.out.println(listOfUsers);

            if(listOfUsers.equals(0))
            {
                throw new RuntimeException("please, First add the users,");
            }
        }
        else
        {
            throw new RuntimeException("unable to upload mobile number");
        }

        return ResponseEntity.ok("Customers mobile numbers added successfully");

    }

    public ResponseEntity updateCustomersMobileNumber(@Header("userId") final long userId,@Header("mobilenumber") final String mobilenumber)
    {
        int rowsUpdated = userRepo.updateMobileNumberByUserId(userId, mobilenumber);

        if (rowsUpdated == 0) {
            throw new RuntimeException("No user found with id " + userId);
        }
        else {
            return ResponseEntity.ok("Customers mobile numbers added successfully");
        }

    }
}
