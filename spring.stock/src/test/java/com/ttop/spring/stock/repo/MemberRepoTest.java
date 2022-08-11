package com.ttop.spring.stock.repo;

import com.ttop.spring.stock.domain.Member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@DataJpaTest
public class MemberRepoTest {

    @Autowired
    MemberRepo memberRepo;

    private Member member;

    @BeforeEach
    public void setUp() throws Exception {
        member = Member.builder().email("email1").password("password1").build();
        member = memberRepo.save(member);
    }

    @Test
    public void testFindByEmail() {
        // given

        // when
        Member memberResult = memberRepo.findByEmail(member.getEmail()).get();

        // then
        assertThat(memberResult).isNotNull();
        assertThat(memberResult.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    public void testExistsByEmail() {
        // given

        // when
        Boolean existsResult = memberRepo.existsByEmail(member.getEmail());

        // then
        assertThat(existsResult).isTrue();
    }

}
