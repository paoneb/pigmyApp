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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("addUserService")
public class AddUserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private UserDetailsRepo userDetailsRepo;

    @Transactional
    public ResponseEntity saveUsers(@Body UserData u) throws Exception {
       AgentNew singleAgent = agentRepo.findByAgentCodeAndBankCode(u.getAgentCode(),u.getBankCode())
               .orElseThrow(() -> new Exception("Agent not found"));

        List<Integer> accountNumbers = u.getUsers()
                .stream()
                .map(UserList::getAccountNumber)
                .toList();

        List<User> existingList =
                userRepo.findByAccountNumberInAndBankCode(accountNumbers, u.getBankCode());

// Convert to a map for quick lookup
        Map<Integer, User> existingUsers =
                existingList.stream()
                        .collect(Collectors.toMap(User::getAccountNumber, k -> k));

        List<User> toSave = new ArrayList<>();

        for (UserList lis : u.getUsers()) {
            User existing = existingUsers.get(lis.getAccountNumber());
            if (existing != null) {
                existing.setSchemeId(lis.getSchemeId());
                existing.setCurrentBalance(lis.getCurrentBalance());
                existing.setCustomerName(lis.getCustomerName());
                existing.setLastDepositDate(lis.getLastDepositDate());

            }
            else {
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
            }
        }
        if (!toSave.isEmpty()) {
            userRepo.saveAll(toSave);
        }

     //   userRepo.saveAll(toSave);
       System.out.println("Inserted users: {}"+ toSave.size());



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


    @Transactional
    public ResponseEntity addMobileNumberService(@Body UploadMobileNumberRequest uploadMobileNumberRequest)
    {
        List<UploadMobileNumber> saveMobileNumber = new ArrayList<>();

        List<Integer> accountNumbers = uploadMobileNumberRequest.getUserDetailsList()
                .stream()
                .map(UserDetails::getAccountNumber)
                .toList();
        List<UploadMobileNumber> existingList =
                userDetailsRepo.findByAccountNumberInAndBankCode(accountNumbers, uploadMobileNumberRequest.getBankCode());

// Convert to a map for quick lookup
        Map<Integer, UploadMobileNumber> existingUsers =
                existingList.stream()
                        .collect(Collectors.toMap(UploadMobileNumber::getAccountNumber, u -> u));

        for (UserDetails details : uploadMobileNumberRequest.getUserDetailsList()) {
            UploadMobileNumber existing = existingUsers.get(details.getAccountNumber());
            if (existing != null) {
                existing.setMobilenumber(details.getMobilenumber());;
            } else {
                UploadMobileNumber newEntry = new UploadMobileNumber();
                newEntry.setAccountNumber(details.getAccountNumber());
                newEntry.setMobilenumber(details.getMobilenumber());
                newEntry.setBankCode(uploadMobileNumberRequest.getBankCode());
                saveMobileNumber.add(newEntry);
            }
        }
         if(!saveMobileNumber.isEmpty()) {
             List<UploadMobileNumber> mn = userDetailsRepo.saveAll(saveMobileNumber);
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
