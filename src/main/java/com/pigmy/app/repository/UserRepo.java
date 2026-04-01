package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import com.pigmy.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByAccountNumberAndBankCode(Integer accountNumber,String bankCode);

   // List<User> findByAgents_AgentCodeAndAgents_BankCode(Integer agentCode,String bankCode);

    @Query("SELECT u FROM User u  WHERE u.agentCode = :agentCode AND u.bankCode = :bankCode")
    List<User> findUsersByAgentCode_bankCode(@Param("agentCode") Integer agentCode,
                                @Param("bankCode") String bankCode);


}
