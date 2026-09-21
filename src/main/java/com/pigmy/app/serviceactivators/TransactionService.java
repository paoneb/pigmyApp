package com.pigmy.app.serviceactivators;


import com.pigmy.app.model.AgentDeposit;
import com.pigmy.app.model.AgentDepositRequest;
import com.pigmy.app.model.Transaction;
import com.pigmy.app.model.peocit.PeocitAgentDeposit;
import com.pigmy.app.model.peocit.PeocitAgentDepositResponse;
import com.pigmy.app.model.peocit.PeocitTransaction;
import com.pigmy.app.model.peocit.PeocitUserCollection;
import com.pigmy.app.model.response.AgentDepositResponse;
import com.pigmy.app.model.response.FetchTransactionResponse;
import com.pigmy.app.model.response.SearchTransactionResponse;
import com.pigmy.app.model.response.UserCollection;
import com.pigmy.app.repository.AgentRepo;
import com.pigmy.app.repository.PeocitTransactionRepo;
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
    private PeocitTransactionRepo peocitTransactionRepo;

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

    public List<FetchTransactionResponse> fetchTransactionPeocit(@Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("date") final LocalDate selectedDate, final Exchange e) {
        List<PeocitTransaction> tr = peocitTransactionRepo.findByAgentCodeAndBankCodeAndCollectedDateAndstatus(agCode, bankCode, selectedDate);

        if (tr.isEmpty()) {
            throw new RuntimeException("No Peocit transactions found for date: " + selectedDate);
        }

        List<FetchTransactionResponse> rs = new ArrayList<>();

        for (PeocitTransaction k : tr) {
            FetchTransactionResponse response = FetchTransactionResponse.builder()
                    .trasactionId(k.getId())
                    .collectedAmount(k.getCollectedAmount())
                    .customerName(k.getCustomerName())
                    .accountNumber(Integer.valueOf(k.getAccountNumber()))
                    .schemeName(k.getSchemename())
                    .status(k.getStatus()).build();
            rs.add(response);
        }

        return rs;
    }


    public List<SearchTransactionResponse> searchTransaction(@Header("bankCode") final String bankCode, @Header("from") String start, @Header("to") String end, @Header("agent") final String agentName, @Header("schemeType") final String schemeType, @Header("collectionStatus") final String collectionStatus, final Exchange e) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<Transaction> tr = transactionRepoRepo.findTransactions(bankCode, startDate, endDate, agentName, schemeType, collectionStatus);

        if (tr.isEmpty()) {
            throw new RuntimeException("No transactions found for date: ");
        }

        List<SearchTransactionResponse> searchrs = new ArrayList<>();

        for (Transaction k : tr) {
            SearchTransactionResponse response = SearchTransactionResponse.builder()
                    .collectedDate(k.getCollectedDate().toString())
                    .collectedAmount(k.getCollectedAmount())
                    .customerName(k.getCustomerName())
                    .accountNumber(k.getAccountNumber())
                    .schemeName(k.getSchemename())
                    .status(k.getStatus())
                    .agentName(k.getAgentname()).build();
            searchrs.add(response);
        }
        return searchrs;
    }


    public List<SearchTransactionResponse> searchTransactionPeocit(@Header("bankCode") final String bankCode, @Header("from") String start, @Header("to") String end, @Header("agent") final String agentName, @Header("schemeType") final String schemeType, @Header("collectionStatus") final String collectionStatus, final Exchange e) {

        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        List<PeocitTransaction> tr = peocitTransactionRepo.findTransactions(bankCode, startDate, endDate, agentName, schemeType, collectionStatus);

        if (tr.isEmpty()) {
            throw new RuntimeException("No Peocit transactions found for date: ");
        }

        List<SearchTransactionResponse> searchrs = new ArrayList<>();

        for (PeocitTransaction k : tr) {
            SearchTransactionResponse response = SearchTransactionResponse.builder()
                    .collectedDate(k.getCollectedDate().toString())
                    .collectedAmount(k.getCollectedAmount())
                    .customerName(k.getCustomerName())
                    .accountNumber(Integer.valueOf(k.getAccountNumber()))
                    .schemeName(k.getSchemename())
                    .status(k.getStatus())
                    .agentName(k.getAgentname()).build();
            searchrs.add(response);
        }
        return searchrs;
    }

    public ResponseEntity deleteTransaction(@Header("transactionId") final long id, final Exchange e) {
        int updated = transactionRepoRepo.markTransactionAsVoid(id);
        if (updated == 0) {
            throw new RuntimeException("Transaction not found with id: " + id);
        }
        return ResponseEntity.ok("Transaction deleted successfully");
    }

    public ResponseEntity deleteTransactionPeocit(@Header("transactionId") final long id, final Exchange e) {
        int updated = peocitTransactionRepo.markTransactionAsVoid(id);
        if (updated == 0) {
            throw new RuntimeException("Peocit Transaction not found with id: " + id);
        }
        return ResponseEntity.ok("Peocit Transaction deleted successfully");
    }


    public void agentDepositingWithMultipleDate(final Exchange exchange) {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositMultipleDatesRequest", AgentDepositRequest.class);
        final AgentDeposit agentDeposit = exchange.getProperty("agentDepositedMultipleDateSuccess", AgentDeposit.class);

        if (agentDeposit.getId() != null) {
            List<Transaction> transactions = exchange.getProperty("transactionDetailsMultipleDates", List.class);


            List<Long> ids = transactions.stream()
                    .map(Transaction::getId)
                    .toList();

            int updatedCount = transactionRepoRepo.bulkUpdateTransactions(
                    "Deposited",
                    agentDeposit.getId(),
                    ids);


            if (!transactions.isEmpty() && updatedCount != 0) {
                List<UserCollection> userCollections = transactions.stream()
                        .map(tr -> {
                            UserCollection l = new UserCollection();
                            l.setSchemeId(tr.getSchemeId());
                            l.setAccountNumber(tr.getAccountNumber());
                            l.setCollectedAmount(tr.getCollectedAmount());
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
                agentDepositResponse.setTotalDepositedAmount(exchange.getProperty("totalCollectedAmountMultipleDate", long.class));
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


    public void PeocitAgentDepositingWithMultipleDate(final Exchange exchange) {
        final AgentDepositRequest peocitAgentDepositrequest = exchange.getProperty("PeocitAgentDepositMultipleDatesRequest", AgentDepositRequest.class);
        final PeocitAgentDeposit peocitAgentDeposit = exchange.getProperty("PeocitAgentDepositedMultipleDateSuccess", PeocitAgentDeposit.class);

        if (peocitAgentDeposit.getId() != null) {
            List<PeocitTransaction> peocitTransactions = exchange.getProperty("PeocittransactionDetailsMultipleDates", List.class);

            List<Long> ids = peocitTransactions.stream()
                    .map(PeocitTransaction::getId)
                    .toList();

            int peocitUpdatedCount = peocitTransactionRepo.bulkUpdateTransactions(
                    "Deposited",
                    peocitAgentDeposit.getId(),
                    ids);


            if (!peocitTransactions.isEmpty() && peocitUpdatedCount != 0) {
                List<PeocitUserCollection> userCollections = peocitTransactions.stream()
                        .map(tr -> {
                            PeocitUserCollection l = new PeocitUserCollection();
                            l.setSchemeAccntNumber(tr.getSchemeId() + tr.getAccountNumber());
                            l.setCollectedAmount(tr.getCollectedAmount());
                            l.setFinalAmount(tr.getFinalAmount());
                            l.setCustomerName(tr.getCustomerName());
                            l.setCollectedDate(tr.getCollectedDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                            return l;
                        })
                        .collect(Collectors.toList());

                PeocitAgentDepositResponse peocitAgentDepositResponse = new PeocitAgentDepositResponse();

                peocitAgentDepositResponse.setAgentCode(peocitAgentDepositrequest.getAgentCode());
                peocitAgentDepositResponse.setBankCode(peocitAgentDepositrequest.getBankCode());
                peocitAgentDepositResponse.setDepositedDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                // peocitAgentDepositResponse.setVpncode(peocitAgentDepositrequest.getVpncode());
                peocitAgentDepositResponse.setUsers(userCollections);
                peocitAgentDepositResponse.setTotalDepositedAmount(exchange.getProperty("totalPeocitCollectedAmountMultipleDate", Long.class));
                LOGGER.info("Peocit Agent Deposited amount successfully", peocitAgentDepositrequest.getAgentCode(), peocitAgentDepositrequest.getBankCode());
                exchange.setProperty("saveToPeocitTransaction", true);
                exchange.getIn().setBody(peocitAgentDepositResponse);

            } else {
                throw new RuntimeException("saving deposting amount failed: " + peocitAgentDepositrequest.getBankCode());
            }


        }
    }


    public void validateDepositingAmountMultipleDate(final Exchange exchange) {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("AgentDepositMultipleDatesRequest", AgentDepositRequest.class);

        LocalDate start = LocalDate.parse(agentDepositrequest.getFrom());
        LocalDate end = LocalDate.parse(agentDepositrequest.getTo());
        List<Transaction> transactions = transactionRepoRepo.findByAgentCodeAndBankCodeAndCollectedDateRangeAndstatus(agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode(), start, end);

        if (transactions.isEmpty()) {
            throw new RuntimeException("No transactions found for date: " + agentDepositrequest.getFrom() + "to" + " " + agentDepositrequest.getTo());
        } else {
            exchange.setProperty("transactionDetailsMultipleDates", transactions);
        }

        long totalCollectedAmount = transactions.stream()
                .map(Transaction::getCollectedAmount).reduce(0L, Long::sum);


        if (totalCollectedAmount == agentDepositrequest.getDepositingAmount()) {
            exchange.setProperty("totalCollectedAmountMultipleDate", totalCollectedAmount);
        } else {
            throw new RuntimeException("Amount mismatch: expected " + totalCollectedAmount);
        }
    }


    public void validatePeocitDepositingAmountMultipleDate(final Exchange exchange) {
        final AgentDepositRequest agentDepositrequest = exchange.getProperty("PeocitAgentDepositMultipleDatesRequest", AgentDepositRequest.class);

        LocalDate peocitstart = LocalDate.parse(agentDepositrequest.getFrom());
        LocalDate peocitend = LocalDate.parse(agentDepositrequest.getTo());
        List<PeocitTransaction> peocitTransactions = peocitTransactionRepo.findByAgentCodeAndBankCodeAndCollectedDateRangeAndstatus(agentDepositrequest.getAgentCode(), agentDepositrequest.getBankCode(), peocitstart, peocitend);

        if (peocitTransactions.isEmpty()) {
            throw new RuntimeException("No transactions found for date: " + agentDepositrequest.getFrom() + "to" + " " + agentDepositrequest.getTo());
        } else {
            exchange.setProperty("PeocittransactionDetailsMultipleDates", peocitTransactions);
        }

        long amnt = peocitTransactions.stream()
                .map(PeocitTransaction::getCollectedAmount).reduce(0L, Long::sum);

        if (amnt == agentDepositrequest.getDepositingAmount()) {
            exchange.setProperty("totalPeocitCollectedAmountMultipleDate", amnt);
        } else {
            throw new RuntimeException("Amount mismatch: expected " + amnt);
        }
    }

    public void fetchPastTransaction(@Header("depositId") long id, @Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("date") String dateRange, @Header("depositedAmount") long amount, final Exchange e) {
        List<Transaction> transactions = transactionRepoRepo.findByAgentDepositId(id);

        if (!transactions.isEmpty()) {
            List<UserCollection> userCollections = transactions.stream()
                    .map(tr -> {
                        UserCollection l = new UserCollection();
                        l.setSchemeId(tr.getSchemeId());
                        l.setAccountNumber(tr.getAccountNumber());
                        l.setCollectedAmount(tr.getCollectedAmount());
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
            agentDepositResponse.setTotalDepositedAmount(amount);
            LOGGER.info("past deposit fetched successfully");
            e.getIn().setBody(agentDepositResponse);
        }
    }

    public void fetchPeocitPastTransaction(@Header("depositId") long id, @Header("agentCode") final Integer agCode, @Header("bankCode") final String bankCode, @Header("date") String dateRange, @Header("depositedAmount") long amount, final Exchange e) {
        List<PeocitTransaction> peocitTransactions = peocitTransactionRepo.findByAgentDepositId(id);

        if (!peocitTransactions.isEmpty()) {
            List<PeocitUserCollection> userCollections = peocitTransactions.stream()
                    .map(tr -> {
                        PeocitUserCollection l = new PeocitUserCollection();
                        l.setSchemeAccntNumber(tr.getSchemeId() + tr.getAccountNumber());
                        l.setCollectedAmount(tr.getCollectedAmount());
                        l.setFinalAmount(tr.getFinalAmount());
                        l.setCustomerName(tr.getCustomerName());
                        l.setCollectedDate(tr.getCollectedDate().format(DateTimeFormatter.ofPattern("dd.MM.yy")));
                        return l;
                    })
                    .collect(Collectors.toList());

            PeocitAgentDepositResponse peocitAgentDepositResponse = new PeocitAgentDepositResponse();

            peocitAgentDepositResponse.setAgentCode(agCode);
            peocitAgentDepositResponse.setBankCode(bankCode);
            peocitAgentDepositResponse.setDepositedDate(LocalDate.parse(dateRange).format((DateTimeFormatter.ofPattern("dd.MM.yy"))));
            peocitAgentDepositResponse.setUsers(userCollections);
            peocitAgentDepositResponse.setTotalDepositedAmount(amount);
            LOGGER.info("Peocit past deposit fetched successfully");
            e.getIn().setBody(peocitAgentDepositResponse);
        }
    }
}
