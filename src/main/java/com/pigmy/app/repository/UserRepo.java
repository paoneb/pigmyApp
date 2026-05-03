package com.pigmy.app.repository;

import com.pigmy.app.model.Transaction;
import com.pigmy.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User,Long> {

    Optional<User> findByAccountNumberAndBankCode(Integer accountNumber,String bankCode);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.mobilenumber = :mobileNumber WHERE u.userId = :userId")
    int updateMobileNumberByUserId(@Param("userId") Long userId,
                                   @Param("mobileNumber") String mobileNumber);


@Query("SELECT u FROM User u  WHERE u.agentCode = :agentCode AND u.bankCode = :bankCode")
    List<User> findUsersByAgentCode_bankCode(@Param("agentCode") Integer agentCode,
                                @Param("bankCode") String bankCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user a " +
            "JOIN customers_details c ON a.account_number = c.account_number " +
            "SET a.mobilenumber = c.mobilenumber " +
            "WHERE a.bank_code = :bankCode",
            nativeQuery = true)
    int updateMobileNumbers(@Param("bankCode") String bankCode);

}
