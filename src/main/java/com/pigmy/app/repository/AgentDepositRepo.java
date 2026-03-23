package com.pigmy.app.repository;

import com.pigmy.app.model.AdminLogin;
import com.pigmy.app.model.AgentDeposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentDepositRepo extends JpaRepository<AgentDeposit,Integer> {
}
