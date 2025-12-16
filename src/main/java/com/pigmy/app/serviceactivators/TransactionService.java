package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.AgentNew;
import com.pigmy.app.model.Transaction;
import com.pigmy.app.model.User;
import com.pigmy.app.model.response.FetchTransactionResponse;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.repository.TransactionRepo;
import com.pigmy.app.repository.UserRepo;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("transactionService")
public class TransactionService {


    @Autowired
    private TransactionRepo transactionRepoRepo;

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private UserRepo userRepo;


    public FetchTransactionResponse addDeposit(@Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("userId") final long userid, @Header("depositAmount") final BigDecimal dsAmount, @Header("depositeDate") Date dt, final Exchange e)
    {

        User customer = userRepo.findById(userid)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        AgentNew agent = agentRepo.findByAgentCodeAndBankCode(agCode,bankCode)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        Instant nowInstant = Instant.now();
        Date todayLegacyDate = Date.from(nowInstant);
        Transaction tx = new Transaction();
        tx.setUser(customer);
        tx.setAgents(agent);
        tx.setDepositAmount(dsAmount);
        tx.setDepositeDate(LocalDate.now());



        Transaction h= transactionRepoRepo.save(tx);
        FetchTransactionResponse rs=FetchTransactionResponse.builder()
                .trasactionId(h.getId())
                .depositAmount(h.getDepositAmount())
                .customerName(h.getUser().getCustomerName())
                .accountNumber(h.getUser().getAccountNumber()).build();

        return rs;



    }

    public List<FetchTransactionResponse> fetchTransaction(@Header("agentCode") final Integer agCode,@Header("bankCode") final String bankCode, @Header("dateRange") final LocalDate daterange, final Exchange e)
    {
         List<Transaction> tr =transactionRepoRepo.findByAgents_AgentCodeAndAgents_bankCodeAndDepositeDate(agCode,bankCode,daterange);
         List<FetchTransactionResponse> rs=new ArrayList<>();

         for(Transaction k:tr)
         {
             FetchTransactionResponse response=FetchTransactionResponse.builder()
                     .trasactionId(k.getId())
                     .depositAmount(k.getDepositAmount())
                     .customerName(k.getUser().getCustomerName())
                     .accountNumber(k.getUser().getAccountNumber()).build();
             rs.add(response);
         }

         return rs;
    }

    public ResponseEntity deleteTransaction(@Header("transactionId") final long id,final Exchange e)
    {
        if (!transactionRepoRepo.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepoRepo.deleteById(id);
        return ResponseEntity.ok("Transaction deleted successfully");


    }
}
