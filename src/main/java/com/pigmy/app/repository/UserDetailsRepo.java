package com.pigmy.app.repository;

import com.pigmy.app.model.UploadMobileNumber;
import com.pigmy.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserDetailsRepo extends JpaRepository<UploadMobileNumber,Long>
{
    @Query(value = "SELECT * FROM customers_details u " +
            "WHERE u.account_number = :accountNumber AND u.bank_code = :bankCode",
            nativeQuery = true)
    Optional<UploadMobileNumber> findByAccountNumberAndBankCode(Integer accountNumber, String bankCode);
}
