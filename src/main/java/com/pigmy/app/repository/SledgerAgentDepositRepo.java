package com.pigmy.app.repository;

import com.pigmy.app.model.peocit.PeocitAgentDeposit;
import com.pigmy.app.model.sledger.SledgerAgentDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface SledgerAgentDepositRepo extends JpaRepository<SledgerAgentDeposit,Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE SledgerAgentDeposit u SET u.depositStatus = 'SUCCESS' WHERE u.id = :id")
   int updatePeocitAgentDesositStatus(long id);


    @Query("SELECT c FROM SledgerAgentDeposit c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.depositDate BETWEEN :startDate AND :endDate  AND c.depositStatus = 'SUCCESS'")
    List<SledgerAgentDeposit> findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(Integer agentCode,
                                                                               String bankCode,
                                                                               LocalDate startDate,
                                                                               LocalDate endDate);

    @Query("SELECT c FROM SledgerAgentDeposit c WHERE c.agentCode = :agentCode AND c.bankCode = :bankCode AND c.depositDate = :pastDate AND c.depositStatus = 'SUCCESS'")
    SledgerAgentDeposit findByAgentCodeAndBankCodeAndDepositDateRangeAndstatus(Integer agentCode,
                                                                              String bankCode,
                                                                              LocalDate pastDate);
}
