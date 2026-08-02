package com.pigmy.app.repository;


import com.pigmy.app.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken,Long> {

    @Transactional
    long deleteByMobileNumber(String mobileNumber);

}
