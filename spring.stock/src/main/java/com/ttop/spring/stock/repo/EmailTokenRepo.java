package com.ttop.spring.stock.repo;


import java.util.Optional;

import com.ttop.spring.stock.domain.EmailToken;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTokenRepo extends JpaRepository<EmailToken, String> {
    Optional<EmailToken> findByUserId(Long userId);
    
    
}