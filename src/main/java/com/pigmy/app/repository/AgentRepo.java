package com.pigmy.app.repository;

import com.pigmy.app.model.AgentNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgentRepo extends JpaRepository<AgentNew,Long> {

    @Query("SELECT a FROM AgentNew a WHERE a.agentCode = :agentCode AND a.bankCode = :bankCode AND LOWER(a.status) = 'active'")
    Optional<AgentNew> findByAgentCodeAndBankCode(Integer agentCode, String bankCode);

    @Query("SELECT a FROM AgentNew a WHERE a.bankCode = :bankCode AND LOWER(a.status) = 'active'")
    List<AgentNew> findAllAgentByBankCode(String bankCode);

}
