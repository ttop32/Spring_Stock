package com.ttop.spring.stock.service;

import javax.mail.MessagingException;

import com.ttop.spring.stock.domain.EmailToken;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.domain.Role;
import com.ttop.spring.stock.dto.MemberEmailDto;
import com.ttop.spring.stock.dto.MemberPasswordDto;
import com.ttop.spring.stock.dto.MemberProfileDto;
import com.ttop.spring.stock.dto.MemberRoleDto;
import com.ttop.spring.stock.dto.MemberSignUpDto;
import com.ttop.spring.stock.error.CustomException;
import com.ttop.spring.stock.error.ErrorCode;
import com.ttop.spring.stock.repo.EmailTokenRepo;
import com.ttop.spring.stock.repo.MemberRepo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@EnableConfigurationProperties
@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class MemberService implements UserDetailsService {
    private MemberRepo memberRepo;
    private EmailService emailService;
    private EmailTokenRepo emailTokenRepo;


    
    public Member registerMember(MemberSignUpDto memberDto) {
        handleEmailExists(memberDto.getEmail());

        Member member = memberDto.toEntity();
        Member memberSaved = memberRepo.save(member);
        return memberSaved;
    }

    public Member getMemberInfo(Member member) {
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);  
        }
        return getMember(member.getId());
    }

    public void removeMember(Member member) {
        Member currentMember = getMember(member.getId());
        memberRepo.delete(currentMember);
    }

    public Member addRoleToUser(Member member, MemberRoleDto memberRoleDto) {
        Member currentMember = getMember(member.getId());
        Member targetMember = getMemberByEmail(memberRoleDto.getTargetMemberEmail());

        Role targetRole=memberRoleDto.getRole();
        log.debug(targetRole.toString());

        checkIsAdmin(currentMember);
        targetMember.addRole(memberRoleDto.getRole());
        return targetMember;
    }

    public Member removeRoleToUser(Member member, MemberRoleDto memberRoleDto) {
        Member currentMember = getMember(member.getId());
        Member targetMember =  getMemberByEmail(memberRoleDto.getTargetMemberEmail());
        checkIsAdmin(currentMember);
        targetMember.removeRole(memberRoleDto.getRole());
        return targetMember;
    }


    public Member changeProfile(MemberProfileDto MemberProfileDto, Member member) {
        Member currentMember = getMember(member.getId());
        currentMember.updateProfile(MemberProfileDto.getNickname(), null);
        return memberRepo.save(currentMember);
    }


    public void changePasswordWithSession(MemberPasswordDto memberPasswordDto, Member member) {
        checkPrevPassword(memberPasswordDto.getPreviousPassword(),member.getId());
        changePassword(memberPasswordDto.getPassword(), member.getId());
    }

    public void changePasswordWithEmailToken(MemberPasswordDto memberPasswordDto) {
        EmailToken emailToken = getEmailToken(memberPasswordDto.getEmailTokenId());
        Member member = getMember(emailToken.getUserId());

        changePassword(memberPasswordDto.getPassword(), member.getId());
    }

    private void changePassword(String password, Long memberId) {
        Member currentMember = getMember(memberId);
        currentMember.updatePassword(password);
        memberRepo.save(currentMember);
    }

    private void checkPrevPassword(String prevPw, Long id){
        Member member=getMember(id);
        if(member.verifyPassword(prevPw)==false){
            throw new CustomException(ErrorCode.INVALID_PREV_PW);            
        }
    }


    public void handleEmailExists(String email) {
        if(checkMemberExistsByEmail(email)){
            throw new CustomException(ErrorCode.EMAIL_DUPLICATION);
        }
    }


    public boolean checkMemberExistsByEmail(String email) {
        return memberRepo.existsByEmail(email);
    }

    public void confirmEmail(String tokenId) {
        // https://programmer93.tistory.com/69
        // https://devkingdom.tistory.com/118
        EmailToken emailToken = getEmailToken(tokenId);

        Member member = getMember(emailToken.getUserId());
        member.verifyEmail();
        emailTokenRepo.deleteById(tokenId);
    }





    public void sendEmailVerifyication(MemberEmailDto memberEmailDto) throws MessagingException {
        emailService.sendVerifyEmail(memberEmailDto);
    }


    public void sendPasswordResetEmail(MemberEmailDto memberEmailDto) throws MessagingException {
        emailService.sendPwResetEmail(memberEmailDto);
    }








    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        if (userEmail == null) {
            throw new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.");  
        }
        return memberRepo.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    private Member getMember(Long id) {
        if (id == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);  
        }
        return memberRepo.findById(id).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Member getMemberByEmail(String email) {
        if (email == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);  
        }
        return memberRepo.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private EmailToken getEmailToken(String tokenId) {
        if (tokenId == null) {
            throw new CustomException(ErrorCode.EMAIL_TOKEN_NOT_FOUND);  
        }
        EmailToken emailToken = emailTokenRepo.findById(tokenId)
                .orElseThrow(() -> new CustomException(ErrorCode.EMAIL_TOKEN_NOT_FOUND));
        checkEmailTokenExpired(emailToken);
        return emailToken;
    }


    private void checkEmailTokenExpired(EmailToken emailToken) {
        if (emailToken.isEmailTokenExpired()) {
            throw new CustomException(ErrorCode.INVALID_EMAIL_TOKEN);
        }
    }

    private void checkIsAdmin(Member member) {
        if (!member.checkMemberHasAdminRole()) {
            throw new CustomException(ErrorCode.INVALID_USER_PERMISSION);
        }
    }

}