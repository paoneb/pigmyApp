package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.*;
import com.pigmy.app.model.peocit.PeocitUser;
import com.pigmy.app.model.peocit.PeocitUserData;
import com.pigmy.app.model.peocit.PeocitUserList;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.repository.PeocitUserRepo;
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
    private PeocitUserRepo peocitUserRepo;

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private UserDetailsRepo userDetailsRepo;

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity saveUsers(@Body UserData u) throws Exception {
        agentRepo.findByAgentCodeAndBankCode(u.getAgentCode(), u.getBankCode())
                .orElseThrow(() -> new Exception("Agent not found"));

        try {

            List<String> accountNumbers = u.getUsers()
                    .stream()
                    .map(UserList::getAccountNumber)
                    .toList();

            List<User> existingList =
                    userRepo.findByAccountNumberInAndBankCode(accountNumbers, u.getBankCode());

// Convert to a map for quick lookup
            Map<String, User> existingUsers =
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

                } else {
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
            System.out.println("Inserted users: {}" + toSave.size());


            return ResponseEntity.ok(" banksoft Customer added successfully");
        } catch (Exception e) {
            throw new RuntimeException("unable Adding banksoft customer failed");
        }
    }

    public List<User> fetchCustomers(@Header("agentCode") final Integer agentCode, @Header("bankCode") final String bankCode) {
        if (agentCode != null) {
            return userRepo.findUsersByAgentCode_bankCode(agentCode, bankCode);

        } else {
            return userRepo.findAll();
        }


    }

    public List<PeocitUser> fetchCustomersPeocit(@Header("agentCode") final Integer agentCode, @Header("bankCode") final String bankCode) {
        if (agentCode != null) {
            return peocitUserRepo.findUsersByAgentCode_bankCode(agentCode, bankCode);

        } else {
            return peocitUserRepo.findAll();

        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity saveUsersPeocit(@Body final PeocitUserData userData) throws Exception {


        agentRepo.findByAgentCodeAndBankCode(userData.getAgentCode(), userData.getBankCode())
                .orElseThrow(() -> new Exception("Agent not found"));

        try {
            List<String> accountNumbersPeocit = userData.getUsers()
                    .stream()
                    .map(PeocitUserList::getAccountNumber)
                    .toList();

            List<PeocitUser> existingPeocitList =
                    peocitUserRepo.findByAccountNumberInAndBankCode(accountNumbersPeocit, userData.getBankCode());

            Map<String, PeocitUser> existingPeocitUsers =
                    existingPeocitList.stream()
                            .collect(Collectors.toMap(PeocitUser::getAccountNumber, k -> k));

            List<PeocitUser> toSavePeocit = new ArrayList<>();

            for (PeocitUserList peocitUserList : userData.getUsers()) {
                PeocitUser existingPeocitUser = existingPeocitUsers.get(peocitUserList.getAccountNumber());
                if (existingPeocitUser != null) {
                    existingPeocitUser.setSchemeId(peocitUserList.getSchemeId());
                    existingPeocitUser.setCurrentBalance(peocitUserList.getCurrentBalance());
                    existingPeocitUser.setCustomerName(peocitUserList.getCustomerName());
                    existingPeocitUser.setLastDepositDate(peocitUserList.getLastDepositDate());

                } else {
                    // first time save
                    PeocitUser peocitUser = new PeocitUser();
                    peocitUser.setSchemeId(peocitUserList.getSchemeId());
                    peocitUser.setAccountNumber(peocitUserList.getAccountNumber());
                    peocitUser.setCustomerName(peocitUserList.getCustomerName());
                    peocitUser.setCurrentBalance(peocitUserList.getCurrentBalance());
                    peocitUser.setLastDepositDate(peocitUserList.getLastDepositDate());
                    peocitUser.setBankCode(userData.getBankCode());
                    peocitUser.setAgentCode(userData.getAgentCode());
                    peocitUser.setVpncode(userData.getVpncode());
                    toSavePeocit.add(peocitUser);
                }
            }
            if (!toSavePeocit.isEmpty()) {
                peocitUserRepo.saveAll(toSavePeocit);
            }

            return ResponseEntity.ok("Peocit Customer added successfully");
        } catch (Exception e) {
            throw new RuntimeException("unable Adding peocit customer failed");
        }

    }


    @Transactional
    public ResponseEntity addMobileNumberService(@Body UploadMobileNumberRequest uploadMobileNumberRequest) {
        List<UploadMobileNumber> saveMobileNumber = new ArrayList<>();

        List<String> accountNumbers = uploadMobileNumberRequest.getUserDetailsList()
                .stream()
                .map(UserDetails::getAccountNumber)
                .toList();
        List<UploadMobileNumber> existingList =
                userDetailsRepo.findByAccountNumberInAndBankCode(accountNumbers, uploadMobileNumberRequest.getBankCode());

// Convert to a map for quick lookup
        Map<String, UploadMobileNumber> existingUsers =
                existingList.stream()
                        .collect(Collectors.toMap(UploadMobileNumber::getAccountNumber, u -> u));

        for (UserDetails details : uploadMobileNumberRequest.getUserDetailsList()) {
            UploadMobileNumber existing = existingUsers.get(details.getAccountNumber());
            if (existing != null) {
                existing.setMobilenumber(details.getMobilenumber());
            } else {
                UploadMobileNumber newEntry = new UploadMobileNumber();
                newEntry.setAccountNumber(details.getAccountNumber());
                newEntry.setMobilenumber(details.getMobilenumber());
                newEntry.setBankCode(uploadMobileNumberRequest.getBankCode());
                saveMobileNumber.add(newEntry);
            }
        }
        if (!saveMobileNumber.isEmpty()) {
            List<UploadMobileNumber> mn = userDetailsRepo.saveAll(saveMobileNumber);
            if (!mn.isEmpty()) {
                Integer listOfUsers = userRepo.updateMobileNumbers(uploadMobileNumberRequest.getBankCode());

                System.out.println(listOfUsers);

                if (listOfUsers.equals(0)) {
                    throw new RuntimeException("please, First add the users,");
                }
            } else {
                throw new RuntimeException("unable to upload mobile number");
            }
        }


        return ResponseEntity.ok("Customers mobile numbers added successfully");

    }


    public ResponseEntity addPeocitMobileNumberService(@Body UploadMobileNumberRequest uploadMobileNumberRequest) {
        List<UploadMobileNumber> saveMobileNumber = new ArrayList<>();

        List<String> accountNumbers = uploadMobileNumberRequest.getUserDetailsList()
                .stream()
                .map(UserDetails::getAccountNumber)
                .toList();
        List<UploadMobileNumber> existingList =
                userDetailsRepo.findByAccountNumberInAndBankCode(accountNumbers, uploadMobileNumberRequest.getBankCode());

// Convert to a map for quick lookup
        Map<String, UploadMobileNumber> existingUsers =
                existingList.stream()
                        .collect(Collectors.toMap(UploadMobileNumber::getAccountNumber, u -> u));

        for (UserDetails details : uploadMobileNumberRequest.getUserDetailsList()) {
            UploadMobileNumber existing = existingUsers.get(details.getAccountNumber());
            if (existing != null) {
                existing.setMobilenumber(details.getMobilenumber());
            } else {
                UploadMobileNumber newEntry = new UploadMobileNumber();
                newEntry.setAccountNumber(details.getAccountNumber());
                newEntry.setMobilenumber(details.getMobilenumber());
                newEntry.setBankCode(uploadMobileNumberRequest.getBankCode());
                saveMobileNumber.add(newEntry);
            }
        }
        if (!saveMobileNumber.isEmpty()) {
            List<UploadMobileNumber> mn = userDetailsRepo.saveAll(saveMobileNumber);
            if (!mn.isEmpty()) {
                Integer listOfUsers = peocitUserRepo.updateMobileNumbers(uploadMobileNumberRequest.getBankCode());

                System.out.println(listOfUsers);

                if (listOfUsers.equals(0)) {
                    throw new RuntimeException("please, First add the users,");
                }
            } else {
                throw new RuntimeException("unable to upload mobile number");
            }
        }


        return ResponseEntity.ok("Customers mobile numbers added successfully");
    }

    public ResponseEntity updateCustomersPeocitMobileNumber(@Header("userId") final long userId, @Header("mobilenumber") final String mobilenumber) {
        int rowsUpdated = peocitUserRepo.updateMobileNumberByUserId(userId, mobilenumber);

        if (rowsUpdated == 0) {
            throw new RuntimeException("No user found with id " + userId);
        } else {
            return ResponseEntity.ok("Peocit Customers mobile numbers added successfully");
        }

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
