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

@Component("transactionService")
public class TransactionService {


    @Autowired
    private TransactionRepo transactionRepoRepo;

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private UserRepo userRepo;


    public void addDeposit(@Header("agentCode") final Integer agCode, @Header("userId") final long userid, @Header("depositAmount") final BigDecimal dsAmount,@Header("depositeDate") Date dt, final Exchange e)
    {

        User customer = userRepo.findById(userid)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        AgentNew agent = agentRepo.findById(agCode)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        Instant nowInstant = Instant.now();
        Date todayLegacyDate = Date.from(nowInstant);
        Transaction tx = new Transaction();
        tx.setUser(customer);
        tx.setAgents(agent);
        tx.setDepositAmount(dsAmount);
        tx.setDepositeDate(LocalDate.now());



        Transaction h= transactionRepoRepo.save(tx);
        System.out.println(h.getAgents().getName());
        System.out.println(h.getUser().getCustomerName());


    }

    public List<FetchTransactionResponse> fetchTransaction(@Header("agentCode") final Integer agCode, @Header("dateRange") final LocalDate daterange, final Exchange e)
    {
         List<Transaction> tr =transactionRepoRepo.findByAgents_AgentCodeAndDepositeDate(agCode,daterange);
         List<FetchTransactionResponse> rs=new ArrayList<>();

         for(Transaction k:tr)
         {
             FetchTransactionResponse response=new FetchTransactionResponse();
             response.setTrasactionId(k.getId());
             response.setAccountNumber(k.getUser().getAccountNumber());
             response.setCustomerName(k.getUser().getCustomerName());
             response.setDepositAmount(k.getDepositAmount());
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
