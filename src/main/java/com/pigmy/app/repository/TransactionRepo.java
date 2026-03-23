package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    List<Transaction> findByAgents_AgentCodeAndAgents_bankCodeAndDepositeDate(Integer agentCode, String bankCode,LocalDate depositeDate);

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
    List<Transaction>  findByDepositeDateRange(LocalDate start,LocalDate end);

}
