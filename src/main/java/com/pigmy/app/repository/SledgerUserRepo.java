package com.pigmy.app.repository;

import com.pigmy.app.model.peocit.PeocitUser;
import com.pigmy.app.model.sledger.SledgerUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SledgerUserRepo extends JpaRepository<SledgerUser,Long> {


    List<SledgerUser> findByAccountNumberInAndBankCode(List<String> accountNumbers, String bankCode);

    @Modifying
    @Transactional
    @Query("UPDATE SledgerUser u SET u.mobilenumber = :mobileNumber WHERE u.userId = :userId")
    int updateMobileNumberByUserId(@Param("userId") Long userId,
                                   @Param("mobileNumber") String mobileNumber);


@Query("SELECT u FROM SledgerUser u  WHERE u.agentCode = :agentCode AND u.bankCode = :bankCode")
    List<SledgerUser> findUsersByAgentCode_bankCode(@Param("agentCode") Integer agentCode,
                                @Param("bankCode") String bankCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE sledger_user a " +
            "JOIN customers_details c ON a.account_number = c.account_number " +
            "SET a.mobilenumber = c.mobilenumber " +
            "WHERE a.bank_code = :bankCode",
            nativeQuery = true)
    int updateMobileNumbers(@Param("bankCode") String bankCode);

}
