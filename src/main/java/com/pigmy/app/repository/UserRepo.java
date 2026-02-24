package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import com.pigmy.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByAccountNumberAndAgents_BankCode(Integer accountNumber,String bankCode);

   // List<User> findByAgents_AgentCodeAndAgents_BankCode(Integer agentCode,String bankCode);

    @Query("SELECT u FROM User u JOIN FETCH u.agents a WHERE a.agentCode = :agentCode AND a.bankCode = :bankCode")
    List<User> findUsersByAgent(@Param("agentCode") Integer agentCode,
                                @Param("bankCode") String bankCode);


}
