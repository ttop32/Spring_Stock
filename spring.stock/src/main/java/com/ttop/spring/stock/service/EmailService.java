package com.ttop.spring.stock.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.ttop.spring.stock.domain.EmailToken;
import com.ttop.spring.stock.domain.Member;
import com.ttop.spring.stock.dto.MemberEmailDto;
import com.ttop.spring.stock.error.CustomException;
import com.ttop.spring.stock.error.ErrorCode;
import com.ttop.spring.stock.repo.EmailTokenRepo;
import com.ttop.spring.stock.repo.MemberRepo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailService {
    // https://programmer93.tistory.com/69

    private final JavaMailSender sender;
    private final MemberRepo memberRepo;
    private final EmailTokenRepo emailTokenRepo;
    
    @Value("${front.web.url}")
    private String frontWebUrl;

    
    public void sendEmail(String toAddress, String title, String body) throws MessagingException {
        // Map<String, Object> result = new HashMap<String, Object>();
        // try {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toAddress);     //email 
        helper.setSubject(title); //email title
        helper.setText(body);       //email body
        sender.send(message);
        //     result.put("resultCode", 200);
        // } catch (MessagingException e) {
        //     e.printStackTrace();
        //     result.put("resultCode", 500);
        // }
        // return result;
    }

    public void sendVerifyEmail(MemberEmailDto memberEmailDto) throws MessagingException{
        EmailToken emailToken = createEmailToken(memberEmailDto.getEmail(),"email");
        String baseUrl = getRedirectBaseUrl(memberEmailDto.getIsFront());
        String emailRedirectUrl = baseUrl+"verify?token="+emailToken.getId();

        String emailTitle = "Spring Stock - Email verification for sign out";
        String emailBody= "\n"
        + "This email is sent you because of account is not yet email verified.\n"
        + "If you are not yet sign out on Spring Stock site, then ignore this email.\n"
        + "To progress to complete sign out on Spring Stock site, \n"
        + "click bellow redirect url link to verify your email for your account. \n"
        + emailRedirectUrl;
        

        sendEmail(memberEmailDto.getEmail(), emailTitle, emailBody);
    }


    public void sendPwResetEmail(MemberEmailDto memberEmailDto) throws MessagingException{
        EmailToken emailToken = createEmailToken(memberEmailDto.getEmail(),"pwreset");
        String baseUrl = getRedirectBaseUrl(memberEmailDto.getIsFront());
        String emailRedirectUrl = baseUrl+"passwordreset?token="+emailToken.getId();

        String emailTitle = "Spring Stock - Reset password";
        String emailBody= "\n"
        + "This email is sent you because password reset request was given.\n"
        + "If you does not request password reset, then ignore this email.\n"
        + "To progress password reset on Spring Stock site, \n"
        + "click bellow redirect url link to change to new account password. \n"
        + emailRedirectUrl;

        sendEmail(memberEmailDto.getEmail(), emailTitle, emailBody);
    }

    
    
    
    private String getRedirectBaseUrl(Boolean isFront){
        if(isFront){
            return frontWebUrl+"/profile/";
        }else{
            return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()+"/member/email/";
        }
    }



    private Member getMemberByEmail(String email) {
        return memberRepo.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }


    private EmailToken createEmailToken(String email,String type) {
        Member member=   getMemberByEmail(email);
        EmailToken emailToken = EmailToken.builder().userId(member.getId()).type(type).build();
        return emailTokenRepo.save(emailToken);
    }


}