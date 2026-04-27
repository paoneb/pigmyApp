package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction,Long> {


    @Query("SELECT c FROM Transaction c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.collectedDate = :collectedDate AND c.status = 'C'")
    List<Transaction> findByAgentCodeAndBankCodeAndCollectedDateAndstatus(Integer agentCode, String bankCode,LocalDate collectedDate);

    @Query("SELECT c FROM Transaction c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.collectedDate BETWEEN :start AND :end  AND c.status = 'C'")
    List<Transaction> findByAgentCodeAndBankCodeAndCollectedDateRangeAndstatus( Integer agentCode,
                                                                               String bankCode,
                                                                               LocalDate start,   // "01.03.2026"
                                                                                LocalDate end);

    List<Transaction> findByAgentDepositId(long id);
}
