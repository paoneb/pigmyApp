package com.pigmy.app.repository;

import com.pigmy.app.model.UploadMobileNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserDetailsRepo extends JpaRepository<UploadMobileNumber,Long>
{
    List<UploadMobileNumber> findByAccountNumberInAndBankCode(List<Integer> accountNumbers, String bankCode);

}
