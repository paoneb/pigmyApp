package com.pigmy.app.repository;

import com.pigmy.app.model.AdminLogin;
import com.pigmy.app.model.AgentNew;
import com.pigmy.app.model.SubBranchDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminLoginRepo extends JpaRepository<AdminLogin,Integer> {
    AdminLogin findByUserNameAndBankCode(String userName, String bankCode);

    @Query("SELECT new com.pigmy.app.model.SubBranchDTO(a.bankCode, a.bankName, a.city) " +
            "FROM AdminLogin a WHERE a.parentId = :parentId")
    List<SubBranchDTO> findByParentId(String parentId);

}
