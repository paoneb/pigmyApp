package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.AgentDeposit;
import com.pigmy.app.model.AgentDepositRequest;
import com.pigmy.app.model.Transaction;
import com.pigmy.app.model.response.AgentDepositResponse;
import com.pigmy.app.model.response.FetchTransactionResponse;
import com.pigmy.app.model.response.UserCollection;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.repository.TransactionRepo;
import com.pigmy.app.repository.UserRepo;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("transactionService")
public class TransactionService {


    @Autowired
    private TransactionRepo transactionRepoRepo;

    @Autowired
    private AgentRepo agentRepo;

    @Autowired
    private UserRepo userRepo;

    private final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);


    public List<FetchTransactionResponse> fetchTransaction(@Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("date") final LocalDate selectedDate, final Exchange e) {
        List<Transaction> tr = transactionRepoRepo.findByAgentCodeAndBankCodeAndCollectedDateAndstatus(agCode, bankCode, selectedDate);

        if (tr.isEmpty()) {
            throw new RuntimeException("No transactions found for date: " + selectedDate);
        }

        List<FetchTransactionResponse> rs = new ArrayList<>();

        for (Transaction k : tr) {
            FetchTransactionResponse response = FetchTransactionResponse.builder()
                    .trasactionId(k.getId())
                    .collectedAmount(k.getCollectedAmount())
                    .customerName(k.getCustomerName())
                    .accountNumber(k.getAccountNumber())
                    .schemeName(k.getSchemename())
                    .status(k.getStatus()).build();
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

            List<Transaction> transactions = exchange.getProperty("transactionDetailsSingleDate", List.class);

            if (transactions.isEmpty()) {
               // throw new RuntimeException("No transactions found for date: " + agentDepositrequest.getDateOfCollectedAmount());
               // throw new RuntimeException("No transactions found for date: " + agentDepositrequest.getDateOfCollectedAmount());
            }

            // Update status
            transactions.forEach(tx -> {
                tx.setStatus("D");
                tx.setAgentDepositId(agentDeposit.getId());
            });

            // Save back (bulk save)
            List<Transaction> trn = transactionRepoRepo.saveAll(transactions);

            if (!trn.isEmpty()) {
                List<UserCollection> userCollections = trn.stream()
                        .map(tr -> {
                            UserCollection l = new UserCollection();
                            l.setSchemeId(tr.getSchemename());
                            l.setAccountNumber(tr.getAccountNumber());
                            l.setCollectedAmount(BigDecimal.valueOf(tr.getCollectedAmount()).setScale(0, RoundingMode.UNNECESSARY));
                            l.setCustomerName(tr.getCustomerName());
                            l.setCollectedDate(tr.getCollectedDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                            return l;
                        })
                        .collect(Collectors.toList());

                AgentDepositResponse agentDepositResponse = new AgentDepositResponse();

                agentDepositResponse.setAgentCode(agentDepositrequest.getAgentCode());
                agentDepositResponse.setBankCode(agentDepositrequest.getBankCode());
                agentDepositResponse.setDepositedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                agentDepositResponse.setUsers(userCollections);
                agentDepositResponse.setTotalDepositedAmount(exchange.getProperty("totalCollectedAmount", BigDecimal.class));
                LOGGER.info("Deposited amount successfully");
                exchange.setProperty("saveTotransaction", true);
                exchange.getIn().setBody(agentDepositResponse);
            } else {
                throw new RuntimeException("saving deposting amount failed: " + agentDepositrequest.getBankCode());
            }

        } else {
            throw new RuntimeException("agentDeposit failed");
        }
    }


    public void agentDepositingWithMultipleDate(final Exchange exchange) {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositMultipleDatesRequest", AgentDepositRequest.class);
        final AgentDeposit agentDeposit = exchange.getProperty("agentDepositedMultipleDateSuccess", AgentDeposit.class);

        if (agentDeposit.getId() != null) {
            List<Transaction> transactions = exchange.getProperty("transactionDetailsMultipleDates", List.class);

            // Update status
            transactions.forEach(tx -> {
                tx.setStatus("D");
                tx.setAgentDepositId(agentDeposit.getId());
            });

            // Save back (bulk save)
            List<Transaction> trn = transactionRepoRepo.saveAll(transactions);


            if (!trn.isEmpty()) {
                List<UserCollection> userCollections = trn.stream()
                        .map(tr -> {
                            UserCollection l = new UserCollection();
                            l.setSchemeId(tr.getSchemeId());
                            l.setAccountNumber(tr.getAccountNumber());
                            l.setCollectedAmount(BigDecimal.valueOf(tr.getCollectedAmount()).setScale(0, RoundingMode.UNNECESSARY));
                            l.setCustomerName(tr.getCustomerName());
                            l.setCollectedDate(tr.getCollectedDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                            return l;
                        })
                        .collect(Collectors.toList());

                AgentDepositResponse agentDepositResponse = new AgentDepositResponse();

                agentDepositResponse.setAgentCode(agentDepositrequest.getAgentCode());
                agentDepositResponse.setBankCode(agentDepositrequest.getBankCode());
                agentDepositResponse.setDepositedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                agentDepositResponse.setUsers(userCollections);
                agentDepositResponse.setTotalDepositedAmount(exchange.getProperty("totalCollectedAmountMultipleDate", BigDecimal.class));
                LOGGER.info("Deposited amount successfully");
                exchange.setProperty("saveTotransaction", true);
                exchange.getIn().setBody(agentDepositResponse);

            } else {
                throw new RuntimeException("saving deposting amount failed: " + agentDepositrequest.getBankCode());
            }
        } else {
            throw new RuntimeException("agentDeposit failed");
        }

    }


    public void validateDepositingAmount(final Exchange exchange) {
       /* final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositRequest", AgentDepositRequest.class);
        List<Transaction> transactions = transactionRepoRepo.findByAgentCodeAndBankCodeAndCollectedDateAndstatus(agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode(), LocalDate.parse(agentDepositrequest.getDateOfCollectedAmount()));

        if (transactions.isEmpty()) {
          //  throw new RuntimeException("No transactions found for date: " + agentDepositrequest.getDateOfCollectedAmount());
        } else {
            exchange.setProperty("transactionDetailsSingleDate", transactions);
        }


        BigDecimal totalCollectedAmount = transactions.stream()
                .map(Transaction::getCollectedAmount)          // Stream<Long>
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)                      // Convert Long → BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add)      // Sum BigDecimals
                .setScale(0, RoundingMode.HALF_UP);

        if (totalCollectedAmount.longValueExact() == agentDepositrequest.getDepositingAmount()) {
            exchange.setProperty("validateDepositingAmount", true);
            exchange.setProperty("totalCollectedAmount", totalCollectedAmount);
        } else {
            throw new RuntimeException("Amount mismatch: expected " + totalCollectedAmount);
        }*/
    }

    public void validateDepositingAmountMultipleDate(final Exchange exchange) {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositMultipleDatesRequest", AgentDepositRequest.class);

        //String[] dates = agentDepositrequest.getDateOfCollectedAmount().toString().split(" to ");
        LocalDate start = LocalDate.parse(agentDepositrequest.getFrom());
        LocalDate end = LocalDate.parse(agentDepositrequest.getTo());
        List<Transaction> transactions = transactionRepoRepo.findByAgentCodeAndBankCodeAndCollectedDateRangeAndstatus(agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode(), start, end);

        if (transactions.isEmpty()) {
            throw new RuntimeException("No transactions found for date: " + agentDepositrequest.getFrom() +"to"+ " "+ agentDepositrequest.getTo());
        } else {
            exchange.setProperty("transactionDetailsMultipleDates", transactions);
        }

        BigDecimal totalCollectedAmount = transactions.stream()
                .map(Transaction::getCollectedAmount)          // Stream<Long>
                .filter(Objects::nonNull)
                .map(BigDecimal::valueOf)                      // Convert Long → BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add)      // Sum BigDecimals
                .setScale(0, RoundingMode.HALF_UP);

        if (totalCollectedAmount.longValueExact() == agentDepositrequest.getDepositingAmount()) {
            exchange.setProperty("totalCollectedAmountMultipleDate", totalCollectedAmount);
        } else {
            throw new RuntimeException("Amount mismatch: expected " + totalCollectedAmount);
        }
    }

    public void fetchPastTransaction(@Header("depositId") long id,@Header("agentCode") final Integer agCode,@Header("bankCode") final String bankCode, @Header("date") String dateRange,@Header("depositedAmount") double amount, final Exchange e) {
        //final AgentDeposit agentDeposit = e.getProperty("pastDeposit", AgentDeposit.class);
        List<Transaction> transactions = transactionRepoRepo.findByAgentDepositId(id);

        if (!transactions.isEmpty()) {
            List<UserCollection> userCollections = transactions.stream()
                    .map(tr -> {
                        UserCollection l = new UserCollection();
                        l.setSchemeId(tr.getSchemeId());
                        l.setAccountNumber(tr.getAccountNumber());
                        l.setCollectedAmount(BigDecimal.valueOf(tr.getCollectedAmount()).setScale(0, RoundingMode.UNNECESSARY));
                        l.setCustomerName(tr.getCustomerName());
                        l.setCollectedDate(tr.getCollectedDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                        return l;
                    })
                    .collect(Collectors.toList());

            AgentDepositResponse agentDepositResponse = new AgentDepositResponse();

            agentDepositResponse.setAgentCode(agCode);
            agentDepositResponse.setBankCode(bankCode);
            agentDepositResponse.setDepositedDate(LocalDate.parse(dateRange).format((DateTimeFormatter.ofPattern("dd.MM.yy"))));
            agentDepositResponse.setUsers(userCollections);
            agentDepositResponse.setTotalDepositedAmount(BigDecimal.valueOf(amount).setScale(0, RoundingMode.UNNECESSARY));
            LOGGER.info("past deposit fetched successfully");
            e.getIn().setBody(agentDepositResponse);

        }
    }
}
