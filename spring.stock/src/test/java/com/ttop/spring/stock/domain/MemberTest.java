package com.ttop.spring.stock.domain;

import org.junit.jupiter.api.Test;



import static org.assertj.core.api.Assertions.*; 


public class MemberTest {


    @Test
    void testUpdatePassword() {
        
        //given
        Member member=Member.builder().email("email2").password("password3").build();
        String newPw="password4";

        //when
        member.updatePassword(newPw);

        //then
        assertThat(member.getPassword()).isNotEqualTo(newPw);
    }

    @Test
    void testVerifyPassword() {
        
        //given
        String prevPw="password544";
        String newPw="password4";
        Member member=Member.builder().email("email2").password(prevPw).build();

        //when
        member.updatePassword(newPw);

        //then
        assertThat(member.verifyPassword(prevPw)).isFalse();
        assertThat(member.verifyPassword(newPw)).isTrue();
        
    }
}
