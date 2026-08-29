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

    @Query(
            value = "SELECT " +
                    "ad.purchase_date, " +
                    "COUNT(a.id) AS active_agents_count " +
                    "FROM (admin_web ad " +
                    "INNER JOIN agents a " +
                    "ON (ad.bank_code = a.bank_code)) " +
                    "WHERE (a.status = 'Active') " +
                    "GROUP BY ad.bank_code, ad.purchase_date",
            nativeQuery = true
    )
    List<Object[]>  findByBankCode(String bankCode);

}
