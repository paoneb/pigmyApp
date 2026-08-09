package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction,Long> {


    @Query("SELECT c FROM Transaction c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.collectedDate = :collectedDate AND c.status = 'Collected'")
    List<Transaction> findByAgentCodeAndBankCodeAndCollectedDateAndstatus(Integer agentCode, String bankCode,LocalDate collectedDate);

    @Query("SELECT c FROM Transaction c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.collectedDate BETWEEN :start AND :end  AND c.status = 'Collected'")
    List<Transaction> findByAgentCodeAndBankCodeAndCollectedDateRangeAndstatus( Integer agentCode,
                                                                               String bankCode,
                                                                               LocalDate start,   // "01.03.2026"
                                                                                LocalDate end);

    List<Transaction> findByAgentDepositId(long id);

    @Query("SELECT c FROM Transaction c WHERE c.bankCode = :bankCode AND c.collectedDate BETWEEN :start AND :end  AND (:schemeType = 'ALL' OR c.schemename = :schemeType) AND (:collectionStatus = 'ALL' OR c.status = :collectionStatus) AND (:agentName = 'ALL' OR c.agentname = :agentName)")
    List<Transaction> findTransactions(String bankCode,LocalDate start,LocalDate end, String agentName,String schemeType, String collectionStatus);

    @Modifying
    @Transactional
    @Query("UPDATE Transaction t SET t.status = :status, t.agentDepositId = :depositId WHERE t.id IN :ids")
    int bulkUpdateTransactions(@Param("status") String status,
                               @Param("depositId") Long depositId,
                               @Param("ids") List<Long> ids);


    @Modifying
    @Transactional
    @Query("UPDATE Transaction t SET t.status = 'Void' WHERE t.id = :id")
    int markTransactionAsVoid(@Param("id") Long id);


}
