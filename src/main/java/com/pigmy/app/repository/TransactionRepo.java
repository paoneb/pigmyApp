package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    List<Transaction> findByAgents_AgentCodeAndDepositeDate(Integer agentCode, LocalDate depositeDate);

}
