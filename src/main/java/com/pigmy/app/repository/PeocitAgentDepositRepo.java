package com.pigmy.app.repository;

import com.pigmy.app.model.AgentDeposit;
import com.pigmy.app.model.peocit.PeocitAgentDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface PeocitAgentDepositRepo extends JpaRepository<PeocitAgentDeposit,Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE PeocitAgentDeposit u SET u.depositStatus = 'SUCCESS' WHERE u.id = :id")
   int updatePeocitAgentDesositStatus(long id);


    @Query("SELECT c FROM PeocitAgentDeposit c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.depositDate BETWEEN :startDate AND :endDate  AND c.depositStatus = 'SUCCESS'")
    List<PeocitAgentDeposit> findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(Integer agentCode,
                                                                               String bankCode,
                                                                               LocalDate startDate,
                                                                               LocalDate endDate);

    @Query("SELECT c FROM PeocitAgentDeposit c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.depositDate = :pastDate AND c.depositStatus = 'SUCCESS'")
    PeocitAgentDeposit findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(Integer agentCode,
                                                                              String bankCode,
                                                                              LocalDate pastDate);
}
