package com.ttop.spring.stock.controller;



import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ttop.spring.stock.domain.EmailToken;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.domain.Role;
import com.ttop.spring.stock.dto.MemberSignUpDto;
import com.ttop.spring.stock.dto.MemberPasswordDto;
import com.ttop.spring.stock.dto.MemberProfileDto;
import com.ttop.spring.stock.dto.MemberRoleDto;
import com.ttop.spring.stock.repo.EmailTokenRepo;
import com.ttop.spring.stock.repo.MemberRepo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class MemberControllerTest extends IntegrationTest{

    @Autowired
    MemberRepo memberRepo;

    @Autowired
    EmailTokenRepo emailTokenRepo;

    Member member;
    Member adminMember;
    String password;

    @BeforeTransaction
    public void accountSetup() {
        password="5password";
        member=Member.builder().email("member@gmail.com").password(password).build();
        adminMember=Member.builder().email("admin@gmail.com").password(password).build();
        adminMember.addRole(Role.ADMIN);
        adminMember.verifyEmail();
        member=memberRepo.save(member);
        adminMember=memberRepo.save(adminMember);
    }

    @AfterTransaction
    public void accountCleanup() {
        memberRepo.deleteById(member.getId());
        memberRepo.deleteById(adminMember.getId());
    }


    @Test
    void testCheckEmailExists() throws Exception {
        //given


        //when
        ResultActions resultActions=requestWithBodyParam(get("/member/email/").param("email",member.getEmail()),"");

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(content().string("true"));

    }

    @Test
    void testCreateMember() throws Exception {

        //given
        MemberSignUpDto memberAuthDto=MemberSignUpDto.builder().email("email@gmail.com").password("123456789").nickname("name").build();

        //when
        ResultActions resultActions=requestWithBodyParam(post("/member/"),memberAuthDto);

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(memberAuthDto.getEmail()));

        Member resultMember=memberRepo.findByEmail(memberAuthDto.getEmail()).get();
        assertThat(resultMember.getNickname()).isEqualTo(memberAuthDto.getNickname());
    }


    
    @WithUserDetails("admin@gmail.com")
    @Test
    void testCreateMemberRole() throws Exception {
        //given
        MemberRoleDto memberRoleDto=MemberRoleDto.builder().targetMemberEmail(member.getEmail()).role(Role.ADMIN).build();

        //when
        ResultActions resultActions=requestWithBodyParam(post("/member/role/"),memberRoleDto);

        //then
        resultActions
        .andExpect(status().isOk());

        Member resultMember=memberRepo.findByEmail(member.getEmail()).get();
        assertThat(resultMember.getRoles().contains(Role.ADMIN)).isTrue();

    }

    
    @WithUserDetails("member@gmail.com")
    @Test
    void testDeleteMember() throws Exception {
        //given
        

        //when
        ResultActions resultActions=requestWithBodyParam(delete("/member/"),"");

        //then
        resultActions
        .andExpect(status().isOk());

        assertThat(memberRepo.findByEmail(member.getEmail()).isPresent()).isFalse();

    }

    
    @WithUserDetails("admin@gmail.com")
    @Test
    void testDeleteMemberRole() throws Exception {
        //given
        MemberRoleDto memberRoleDto=MemberRoleDto.builder().targetMemberEmail(member.getEmail()).role(Role.MEMBER).build();

        //when
        ResultActions resultActions=requestWithBodyParam(delete("/member/role/"),memberRoleDto);

        //then
        resultActions
        .andExpect(status().isOk());

        Member resultMember=memberRepo.findByEmail(member.getEmail()).get();
        assertThat(resultMember.getRoles().contains(Role.MEMBER)).isFalse();
    }




    @WithUserDetails("member@gmail.com")
    @Test
    void testReadMember() throws Exception {
        //given

        //when
        ResultActions resultActions=requestWithBodyParam(get("/member/"),"");

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value(member.getEmail()));
    }



    
    @WithUserDetails("member@gmail.com")
    @Test
    void testUpdateMemberPassword() throws Exception {
        //given
        MemberPasswordDto memberPasswordDto = MemberPasswordDto.builder().password("password44").previousPassword(password).build();

        //when
        ResultActions resultActions=requestWithBodyParam(put("/member/password/prevpw"),memberPasswordDto);

        //then
        resultActions
        .andExpect(status().isOk());

        Member resultMember=memberRepo.findByEmail(member.getEmail()).get();
        assertThat(resultMember.getPassword()).isNotEqualTo(memberPasswordDto.getPreviousPassword());
        assertThat(resultMember.verifyPassword(memberPasswordDto.getPassword())).isTrue();

        
    }

    @Test
    void testUpdateMemberPasswordWithEmailToken() throws Exception {

        //given
        EmailToken emailToken=EmailToken.builder().userId(member.getId()).build();
        emailToken=emailTokenRepo.save(emailToken);
        MemberPasswordDto memberPasswordDto = MemberPasswordDto.builder().password("password44").emailTokenId(emailToken.getId()).build();
        String prevPw=member.getPassword();
    
        //when
        ResultActions resultActions=requestWithBodyParam(put("/member/password/emailtoken"),memberPasswordDto);

        //then
        resultActions
        .andExpect(status().isOk());

        Member resultMember=memberRepo.findByEmail(member.getEmail()).get();
        assertThat(resultMember.getPassword()).isNotEqualTo(prevPw);
        assertThat(resultMember.verifyPassword(memberPasswordDto.getPassword())).isTrue();

        

    }

    @WithUserDetails("member@gmail.com")
    @Test
    void testUpdateMemberUserInfo() throws Exception {

        //given
        MemberProfileDto memberProfileDto=MemberProfileDto.builder().nickname("newname").build();
    
        //when
        ResultActions resultActions=requestWithBodyParam(put("/member/userinfo"),memberProfileDto);

        //then
        resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.nickname").value(memberProfileDto.getNickname()));

        Member resultMember=memberRepo.findByEmail(member.getEmail()).get();
        assertThat(resultMember.getNickname()).isEqualTo(memberProfileDto.getNickname());


    }

    @Test
    void testVerifyEmail() throws Exception {

        //given
        EmailToken emailToken=EmailToken.builder().userId(member.getId()).build();
        emailToken=emailTokenRepo.save(emailToken);
    
        //when
        ResultActions resultActions=requestWithBodyParam(get("/member/email/verify").param("token", emailToken.getId()),"");

        //then
        resultActions
        .andExpect(status().isOk());

        Member resultMember=memberRepo.findByEmail(member.getEmail()).get();
        assertThat(resultMember.getEmailVerified()).isTrue();

    }

    @WithAnonymousUser
    @Test
    public void login_success() throws Exception {
        //given
    
        //when
        ResultActions resultActions=requestWithBodyParam(post("/user/login").param("username",adminMember.getEmail()).param("password",password),"");
        
        //then
        resultActions.andExpect(authenticated());

        // mockMvc.perform(formLogin().user(member.getEmail()).password(password))
        // .andExpect(authenticated());
        
    }


    @WithAnonymousUser
    @Test
    public void login_fail() throws Exception {
        //given
    
        //when
        ResultActions resultActions=requestWithBodyParam(post("/user/login").param("username",member.getEmail()).param("password","564uj4"),"");
        
        //then
        resultActions.andExpect(unauthenticated());
        
        
    }


}
