package com.pigmy.app.repository;

import com.pigmy.app.model.AgentNew;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentRepo extends JpaRepository<AgentNew,Long> {

    Optional<AgentNew> findByAgentCodeAndBankCode(Integer agentCode, String bankCode);

    List<AgentNew> findAllAgentByBankCode(String bankCode);

}
