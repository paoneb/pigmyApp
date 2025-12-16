package com.pigmy.app.repository;

import com.pigmy.app.model.AdminLogin;
import com.pigmy.app.model.AgentNew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLoginRepo extends JpaRepository<AdminLogin,Integer> {
    AdminLogin findByUserNameAndBankCode(String userName, String bankCode);
}
