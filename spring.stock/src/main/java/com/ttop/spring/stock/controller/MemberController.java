package com.ttop.spring.stock.controller;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.dto.MemberEmailDto;
import com.ttop.spring.stock.dto.MemberPasswordDto;
import com.ttop.spring.stock.dto.MemberPasswordDto.EmailCheck;
import com.ttop.spring.stock.dto.MemberPasswordDto.PwCheck;
import com.ttop.spring.stock.dto.MemberProfileDto;
import com.ttop.spring.stock.dto.MemberRoleDto;
import com.ttop.spring.stock.dto.MemberSignInDto;
import com.ttop.spring.stock.dto.MemberSignUpDto;
import com.ttop.spring.stock.service.MemberService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/member")
public class MemberController {

    MemberService memberService;

    AuthenticationManager authenticationManager;

    @PostMapping("")
    public Member createMember(@Valid @RequestBody MemberSignUpDto memberDto) {
        return memberService.registerMember(memberDto);
    }

    @GetMapping("")
    public Member readMember(@AuthenticationPrincipal Member member) {
        return memberService.getMemberInfo(member);
    }

    @DeleteMapping("")
    public void deleteMember(@AuthenticationPrincipal Member member) {
        memberService.removeMember(member);
    }

    @PutMapping("/userinfo")
    public Member updateMemberUserInfo(@RequestBody MemberProfileDto memberProfileDto,
            @AuthenticationPrincipal Member member) {
        return memberService.changeProfile(memberProfileDto, member);
    }

    @PutMapping("/password/prevpw")
    public void updateMemberPassword(@RequestBody @Validated(PwCheck.class) MemberPasswordDto memberPasswordDto,
            @AuthenticationPrincipal Member member) {
        memberService.changePasswordWithSession(memberPasswordDto, member);
    }

    @PutMapping("/password/emailtoken")
    public void updateMemberPasswordWithEmailToken(
            @RequestBody @Validated(EmailCheck.class) MemberPasswordDto memberPasswordDto) {
                log.debug("aaa");
        memberService.changePasswordWithEmailToken(memberPasswordDto);
    }

    @PostMapping("/role")
    public void createMemberRole(@RequestBody MemberRoleDto memberRoleDto, @AuthenticationPrincipal Member member) {
        memberService.addRoleToUser(member, memberRoleDto);
    }

    @DeleteMapping("/role")
    public void deleteMemberRole(@RequestBody MemberRoleDto memberRoleDto, @AuthenticationPrincipal Member member) {
        memberService.removeRoleToUser(member, memberRoleDto);
    }

    @GetMapping("/email")
    public Boolean checkEmailExists(@RequestParam String email) {
        return memberService.checkMemberExistsByEmail(email);
    }

    @GetMapping("/email/verify")
    public void verifyEmail(@RequestParam String token) {
        memberService.confirmEmail(token);
    }

    @PostMapping("/email/send/emailverification")
    public void sendEmailVerifyication(@RequestBody MemberEmailDto memberEmailDto) throws MessagingException {
        memberService.sendEmailVerifyication(memberEmailDto);
    }

    @PostMapping("/email/send/passwordreset")
    public void sendEmailPasswordReset(@RequestBody MemberEmailDto memberEmailDto) throws MessagingException {
        memberService.sendPasswordResetEmail(memberEmailDto);
    }

    @PostMapping("/login")
    public Authentication login(@RequestBody MemberSignInDto memberSignInDto, HttpSession session) {

        Authentication authentication = authenticationManager.authenticate(memberSignInDto.toAuthToken());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());
        return authentication;

    }

}
