package com.ttop.spring.stock.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import com.ttop.spring.stock.domain.EmailToken;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.domain.Role;
import com.ttop.spring.stock.dto.MemberSignUpDto;
import com.ttop.spring.stock.dto.MemberPasswordDto;
import com.ttop.spring.stock.dto.MemberProfileDto;
import com.ttop.spring.stock.dto.MemberRoleDto;
import com.ttop.spring.stock.repo.EmailTokenRepo;
import com.ttop.spring.stock.repo.MemberRepo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepo memberRepo;
    @Mock
    private EmailTokenRepo emailTokenRepo;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private MemberService memberService;







    Member memberAdmin;
    Member memberTarget;
    String prevPw;
    @BeforeEach
    public void setUp() throws Exception {
        // MockitoAnnotations.initMocks(this);
        prevPw="password2";
        memberAdmin = Member.builder().email("email1").password(prevPw).build();
        memberAdmin.setTestId((long) 1);

        memberAdmin.addRole(Role.ADMIN);
        memberTarget = Member.builder().email("email2").password(prevPw).build();
        memberTarget.setTestId((long)2);
        
    }






    @Test
    void testAddRoleToUser() {

        // given
        MemberRoleDto memberRoleDto = MemberRoleDto.builder().targetMemberEmail(memberTarget.getEmail())
                .role(Role.ADMIN).build();

        given(memberRepo.findById(memberAdmin.getId())).willReturn(Optional.of(memberAdmin));
        given(memberRepo.findByEmail(memberTarget.getEmail())).willReturn(Optional.of(memberTarget));

        // when
        Member member = memberService.addRoleToUser(memberAdmin, memberRoleDto);

        // then
        assertThat(member).isNotNull();
        assertThat(member.getEmail()).isEqualTo(memberTarget.getEmail());
        assertThat(member.getRoles().contains(Role.ADMIN)).isTrue();

    }

    @Test
    void testRemoveRoleToUser() {
        // given
        memberTarget.addRole(Role.ADMIN);
        MemberRoleDto memberRoleDto = MemberRoleDto.builder().targetMemberEmail(memberTarget.getEmail())
                .role(Role.ADMIN).build();

        given(memberRepo.findById(memberAdmin.getId())).willReturn(Optional.of(memberAdmin));
        given(memberRepo.findByEmail(memberTarget.getEmail())).willReturn(Optional.of(memberTarget));


        // when
        Member member = memberService.removeRoleToUser(memberAdmin, memberRoleDto);

        // then
        assertThat(member).isNotNull();
        assertThat(member.getEmail()).isEqualTo(memberTarget.getEmail());
        assertThat(member.getRoles().contains(Role.ADMIN)).isFalse();
    }

    @Test
    void testChangePasswordWithEmailToken() {
        // given
        MemberPasswordDto memberPasswordDto = MemberPasswordDto.builder().emailTokenId("testId").password("password5").build();
        EmailToken emailToken = EmailToken.builder().userId(memberTarget.getId()).build();

        given(memberRepo.findById(memberTarget.getId())).willReturn(Optional.of(memberTarget));
        given(emailTokenRepo.findById(any())).willReturn(Optional.of(emailToken));

        // when
        memberService.changePasswordWithEmailToken(memberPasswordDto);

        // then
        assertThat(memberTarget.getPassword()).isNotEqualTo(prevPw);
    }

    @Test
    void testChangePasswordWithSession() {

        // given
        MemberPasswordDto memberPasswordDto = MemberPasswordDto.builder().password("newpw").previousPassword(prevPw).build();
        given(memberRepo.findById(memberTarget.getId())).willReturn(Optional.of(memberTarget));

        // when
        memberService.changePasswordWithSession(memberPasswordDto, memberTarget);

        // then
        assertThat(memberTarget.verifyPassword(prevPw)).isFalse();
    }

    @Test
    void testChangeProfile() {

        // given
        MemberProfileDto memberProfileDto = MemberProfileDto.builder().nickname("name4").build();
        given(memberRepo.findById(memberTarget.getId())).willReturn(Optional.of(memberTarget));

        // when
        memberService.changeProfile(memberProfileDto, memberTarget);

        // then
        assertThat(memberTarget.getNickname()).isEqualTo(memberProfileDto.getNickname());
    }

    @Test
    void testCheckMemberExistsByEmail() {
        // given
        given(memberRepo.existsByEmail(memberTarget.getEmail())).willReturn(true);

        // when
        Boolean result = memberService.checkMemberExistsByEmail(memberTarget.getEmail());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void testConfirmEmail() {
        // given
        EmailToken emailToken = EmailToken.builder().userId(memberTarget.getId()).build();
        emailToken.setTestId("testId");
        given(memberRepo.findById(memberTarget.getId())).willReturn(Optional.of(memberTarget));
        given(emailTokenRepo.findById(emailToken.getId())).willReturn(Optional.of(emailToken));

        // when
        memberService.confirmEmail(emailToken.getId());

        // then
        assertThat(memberTarget.getEmailVerified()).isTrue();
    }

    @Test
    void testGetMemberInfo() {

        // given
        given(memberRepo.findById(memberTarget.getId())).willReturn(Optional.of(memberTarget));

        // when
        Member mebmerResult = memberService.getMemberInfo(memberTarget);

        // then
        assertThat(mebmerResult.getEmail()).isEqualTo(memberTarget.getEmail());
    }

    @Test
    void testLoadUserByUsername() {
        // given
        given(memberRepo.findByEmail(memberTarget.getEmail())).willReturn(Optional.of(memberTarget));

        // when
        UserDetails user = memberService.loadUserByUsername(memberTarget.getEmail());

        // then
        assertThat(user.getUsername()).isEqualTo(memberTarget.getEmail());
    }

    @Test
    void testRegisterMember() {

        // given
        MemberSignUpDto memberAuthDto = MemberSignUpDto.builder().email("email2").password("password3").build();
        Member memberTarget = memberAuthDto.toEntity();
        given(memberRepo.save(any())).willReturn(memberTarget);

        // when
        Member memberResult = memberService.registerMember(memberAuthDto);

        // then
        assertThat(memberResult.getEmail()).isEqualTo(memberTarget.getEmail());
    }

}
