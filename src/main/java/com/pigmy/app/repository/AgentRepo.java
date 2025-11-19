package com.pigmy.app.repository;

import com.pigmy.app.model.AgentNew;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepo extends JpaRepository<AgentNew,Integer> {
}
