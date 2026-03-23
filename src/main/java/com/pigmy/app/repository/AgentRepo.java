package com.pigmy.app.repository;

import com.pigmy.app.model.AgentNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgentRepo extends JpaRepository<AgentNew,Long> {

    @Query("SELECT a FROM AgentNew a WHERE a.agentCode = :agentCode AND a.bankCode = :bankCode")
    Optional<AgentNew> findByAgentCodeAndBankCode(Integer agentCode, String bankCode);

    List<AgentNew> findAllAgentByBankCode(String bankCode);

}
