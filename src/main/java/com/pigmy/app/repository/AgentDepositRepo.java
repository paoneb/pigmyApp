package com.pigmy.app.repository;

import com.pigmy.app.model.AgentDeposit;
import com.pigmy.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface AgentDepositRepo extends JpaRepository<AgentDeposit,Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE AgentDeposit u SET u.depositStatus = 'SUCCESS' WHERE u.id = :id")
   int updateAgentDesositStatus(long id);


    @Query("SELECT c FROM AgentDeposit c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.depositDate BETWEEN :start AND :end  AND c.depositStatus = 'SUCCESS'")
    List<AgentDeposit> findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(Integer agentCode,
                                                                               String bankCode,
                                                                               LocalDate start,
                                                                               LocalDate end);

    @Query("SELECT c FROM AgentDeposit c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.depositDate = :pastDate AND c.depositStatus = 'SUCCESS'")
    AgentDeposit findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(Integer agentCode,
                                                                              String bankCode,
                                                                              LocalDate pastDate);
}
