package com.ttop.spring.stock.dto;


import lombok.*;

import java.time.LocalDateTime;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
// import java.util.regex.Pattern;

import com.ttop.spring.stock.domain.Member;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MemberSignUpDto {

    // @SafeHtml
    @NotBlank
    @Size(max=200)
    @Email
    private String email;
    
    @Size(min=8,max=50)
    @NotBlank
    private String password;
    
    @Size(min=3,max=15)
    @NotBlank
    @Pattern(regexp =  "^[a-zA-Z0-9]+$", message="only allow alphabet and numeric")
    private String nickname;
    
    public Member toEntity(){
        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .authType("base")
                .build();
    }

}