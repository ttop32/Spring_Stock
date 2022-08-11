package com.ttop.spring.stock.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.ttop.spring.stock.domain.Member;


public interface MemberRepo extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);    

    Boolean existsByEmail(String email);

}
