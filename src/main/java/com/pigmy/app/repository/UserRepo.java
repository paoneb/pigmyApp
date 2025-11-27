package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import com.pigmy.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByAccountNumber(Integer accountNumber);

    List<User> findByAgents_AgentCode(Integer agentCode);


}
