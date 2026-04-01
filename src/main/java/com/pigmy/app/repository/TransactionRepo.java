package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction,Long> {

  /*  List<Transaction> findByAgents_AgentCodeAndAgents_bankCodeAndDepositeDate(Integer agentCode, String bankCode,LocalDate depositeDate);

    @Query("""
    SELECT DISTINCT t
    FROM Transaction t
    JOIN FETCH t.agents a
    JOIN FETCH t.user u
    LEFT JOIN FETCH t.agentDeposit ad
    LEFT JOIN FETCH u.agents ua
    LEFT JOIN FETCH ad.agents aa
    WHERE t.depositeDate = :depositeDate
""")
    List<Transaction> findByDepositeDate(LocalDate depositeDate);

    @Query("""
    SELECT DISTINCT t
    FROM Transaction t
    JOIN FETCH t.agents a
    JOIN FETCH t.user u
    LEFT JOIN FETCH t.agentDeposit ad
    LEFT JOIN FETCH u.agents ua
    LEFT JOIN FETCH ad.agents aa
    WHERE t.depositeDate BETWEEN :start AND :end AND t.status = 'C'
""")
    List<Transaction>  findByDepositeDateRange(LocalDate start,LocalDate end);*/

    @Query("SELECT c FROM Transaction c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.collectedDate = :collectedDate AND c.status = 'C'")
    List<Transaction> findByAgentCodeAndBankCodeAndCollectedDateAndstatus(Integer agentCode, String bankCode,LocalDate collectedDate);

    //@Query("SELECT c FROM Transaction c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.collectedDate BETWEEN :start AND :end  AND c.status = 'C'")
   /* @Query("SELECT c FROM Transaction c " +
            "WHERE c.agentCode = :agentCode " +
            "AND c.bankCode = :bankCode " +
            "AND FUNCTION('STR_TO_DATE', c.collectedDate, '%dd.%mm.%YYYY') " +
            "BETWEEN FUNCTION('STR_TO_DATE', :start, '%dd.%mm.%YYYY') " +
            "AND FUNCTION('STR_TO_DATE', :end, '%dd.%mm.%YYYY') " +
            "AND c.status = 'C'")*/

 /*   @Query(value = """
           SELECT *
           FROM transaction_dummy t
           WHERE t.agent_code = :agentCode
             AND t.bank_code = :bankCode
             AND STR_TO_DATE(t.collected_date, '%d.%m.%Y')
                 BETWEEN STR_TO_DATE(:start, '%d.%m.%Y')
                     AND STR_TO_DATE(:end, '%d.%m.%Y')
             AND t.status = 'C'
           """, nativeQuery = true)*/

    @Query("SELECT c FROM Transaction c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.collectedDate BETWEEN :start AND :end  AND c.status = 'C'")
    List<Transaction> findByAgentCodeAndBankCodeAndCollectedDateRangeAndstatus( Integer agentCode,
                                                                               String bankCode,
                                                                               LocalDate start,   // "01.03.2026"
                                                                                LocalDate end);

}
