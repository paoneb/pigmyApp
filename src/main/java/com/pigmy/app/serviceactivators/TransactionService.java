package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.*;
import com.pigmy.app.model.response.AgentDepositResponse;
import com.pigmy.app.model.response.FetchTransactionResponse;
import com.pigmy.app.model.response.UserCollection;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.repository.TransactionRepo;
import com.pigmy.app.repository.UserRepo;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Component("transactionService")
public class TransactionService {


    @Autowired
    private TransactionRepo transactionRepoRepo;

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private UserRepo userRepo;


    public FetchTransactionResponse addDeposit(@Header("agentCode") final Integer agCode, @Header("schemename") final String schemename, @Header("ledgergroup") final String ledgergroup, @Header("collectiontype") final String collectiontype, @Header("customername") final String customername, @Header("bankCode") final String bankCode, @Header("userId") final long userid, @Header("depositAmount") final Double dsAmount, @Header("depositeDate") Date dt, final Exchange e) {

        User customer = userRepo.findById(userid)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        AgentNew agent = agentRepo.findByAgentCodeAndBankCode(agCode, bankCode)
                .orElseThrow(() -> new RuntimeException("Agent not found"));
        Transaction tx = new Transaction();
        tx.setUser(customer);
        tx.setAgents(agent);
        tx.setCollectedAmount(dsAmount);
        tx.setDepositeDate(LocalDate.now());
        tx.setCollectiontype(collectiontype);
        tx.setLedgergroup(ledgergroup);
        tx.setSchemename(schemename);
        tx.setStatus("C");


        Transaction h = transactionRepoRepo.save(tx);
        FetchTransactionResponse rs = FetchTransactionResponse.builder()
                .trasactionId(h.getId())
                .depositAmount(h.getCollectedAmount())
                .customerName(h.getUser().getCustomerName())
                .accountNumber(h.getUser().getAccountNumber()).build();

        return rs;


    }

    public List<FetchTransactionResponse> fetchTransaction(@Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("dateRange") final LocalDate daterange, final Exchange e) {
        List<Transaction> tr = transactionRepoRepo.findByAgents_AgentCodeAndAgents_bankCodeAndDepositeDate(agCode, bankCode, daterange);
        List<FetchTransactionResponse> rs = new ArrayList<>();

        for (Transaction k : tr) {
            FetchTransactionResponse response = FetchTransactionResponse.builder()
                    .trasactionId(k.getId())
                    .depositAmount(k.getCollectedAmount())
                    .customerName(k.getUser().getCustomerName())
                    .accountNumber(k.getUser().getAccountNumber()).build();
            rs.add(response);
        }

        return rs;
    }

    public ResponseEntity deleteTransaction(@Header("transactionId") final long id, final Exchange e) {
        if (!transactionRepoRepo.existsById(id)) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        transactionRepoRepo.deleteById(id);
        return ResponseEntity.ok("Transaction deleted successfully");


    }

    public void agentDepositingWithDate(final Exchange exchange) {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositRequest", AgentDepositRequest.class);
        final AgentDeposit agentDeposit = exchange.getProperty("agentDepositedSuccess", AgentDeposit.class);

        if (agentDeposit.getId() != null) {
            List<Transaction> transactions = transactionRepoRepo.findByDepositeDate(LocalDate.parse(agentDepositrequest.getDateOfCollectedAmount()));
            if (transactions.isEmpty()) {
                throw new RuntimeException("No transactions found for date: " + agentDepositrequest.getDateOfCollectedAmount());
            }

            // Update status
            transactions.forEach(tx -> {
                tx.setStatus("D");
                tx.setAgentDeposit(agentDeposit);
            });

            // Save back (bulk save)
            List<Transaction> trn = transactionRepoRepo.saveAll(transactions);

            List<UserCollection> userCollections = trn.stream()
                    .map(tr -> {
                        UserCollection l = new UserCollection();
                        l.setSchemeId(tr.getSchemename());
                        l.setAccountNumber(tr.getUser().getAccountNumber());
                        l.setCollectedAmount(BigDecimal.valueOf(tr.getCollectedAmount()).setScale(0, RoundingMode.UNNECESSARY));
                        l.setCustomerName(tr.getUser().getCustomerName());
                        l.setCollectedDate(tr.getDepositeDate().toString());
                        return l;
                    })
                    .collect(Collectors.toList());

            AgentDepositResponse agentDepositResponse = new AgentDepositResponse();

            agentDepositResponse.setAgentCode(agentDepositrequest.getAgentCode());
            agentDepositResponse.setBankCode(agentDepositrequest.getBankCode());
            agentDepositResponse.setDepositedDate(LocalDate.now().toString());
            agentDepositResponse.setUsers(userCollections);
            agentDepositResponse.setTotalCollectedAmount(exchange.getProperty("totalCollectedAmount",BigDecimal.class));
            exchange.getIn().setBody(agentDepositResponse);

        }
    }


    public void agentDepositingWithMultipleDate(final Exchange exchange) {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositMultipleDatesRequest", AgentDepositRequest.class);
        final AgentDeposit agentDeposit = exchange.getProperty("agentDepositedMultipleDateSuccess", AgentDeposit.class);

        if (agentDeposit.getId() != null) {
            List<Transaction> transactions = exchange.getProperty("transactionDetailsMultipleDates",List.class);

            // Update status
            transactions.forEach(tx -> {
                tx.setStatus("D");
                tx.setAgentDeposit(agentDeposit);
            });

            // Save back (bulk save)
            List<Transaction> trn = transactionRepoRepo.saveAll(transactions);

            List<UserCollection> userCollections = trn.stream()
                    .map(tr -> {
                        UserCollection l = new UserCollection();
                        l.setSchemeId(tr.getSchemename());
                        l.setAccountNumber(tr.getUser().getAccountNumber());
                        l.setCollectedAmount(BigDecimal.valueOf(tr.getCollectedAmount()).setScale(0, RoundingMode.UNNECESSARY));
                        l.setCustomerName(tr.getUser().getCustomerName());
                        l.setCollectedDate(tr.getDepositeDate().toString());
                        return l;
                    })
                    .collect(Collectors.toList());

            AgentDepositResponse agentDepositResponse = new AgentDepositResponse();

            agentDepositResponse.setAgentCode(agentDepositrequest.getAgentCode());
            agentDepositResponse.setBankCode(agentDepositrequest.getBankCode());
            agentDepositResponse.setDepositedDate(LocalDate.now().toString());
            agentDepositResponse.setUsers(userCollections);
            agentDepositResponse.setTotalCollectedAmount(exchange.getProperty("totalCollectedAmountMultipleDate",BigDecimal.class));
            exchange.getIn().setBody(agentDepositResponse);

        }
    }



    public void validateDepositingAmount(final Exchange exchange)
    {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositRequest", AgentDepositRequest.class);
        List<Transaction> transactions = transactionRepoRepo.findByDepositeDate(LocalDate.parse(agentDepositrequest.getDateOfCollectedAmount()));

        BigDecimal totalCollectedAmount = transactions.stream()
                .map(Transaction::getCollectedAmount)          // Stream<Long>
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)                      // Convert Long → BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add)      // Sum BigDecimals
                .setScale(0, RoundingMode.HALF_UP);
        if(totalCollectedAmount.longValueExact()==agentDepositrequest.getDepositingAmount())
        {
            exchange.setProperty("validateDepositingAmount",true);
            exchange.setProperty("totalCollectedAmount",totalCollectedAmount);
        }
        else
        {
            throw new RuntimeException("Amount mismatch: expected " + totalCollectedAmount);
        }
    }

    public void validateDepositingAmountMultipleDate(final Exchange exchange)
    {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositMultipleDatesRequest", AgentDepositRequest.class);

        String[] dates = agentDepositrequest.getDateOfCollectedAmount().split(" to ");
        LocalDate start = LocalDate.parse(dates[0].trim());
        LocalDate end = LocalDate.parse(dates[1].trim());
        List<Transaction> transactions = transactionRepoRepo.findByDepositeDateRange(start,end);

        if (transactions.isEmpty()) {
            throw new RuntimeException("No transactions found for date: " + agentDepositrequest.getDateOfCollectedAmount());
        }
        else
        {
            exchange.setProperty("transactionDetailsMultipleDates",transactions);
        }

        BigDecimal totalCollectedAmount = transactions.stream()
                .map(Transaction::getCollectedAmount)          // Stream<Long>
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)                      // Convert Long → BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add)      // Sum BigDecimals
                .setScale(0, RoundingMode.HALF_UP);

        if(totalCollectedAmount.longValueExact()==agentDepositrequest.getDepositingAmount())
        {
            exchange.setProperty("totalCollectedAmountMultipleDate",totalCollectedAmount);
        }
        else
        {
            throw new RuntimeException("Amount mismatch: expected " + totalCollectedAmount);
        }
    }
}
